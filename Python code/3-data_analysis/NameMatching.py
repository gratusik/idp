#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Created on Fri Mar 6 22:42:27 2020
@author: Francesco
"""

import config as cf
import pymongo as pm
import requests as r
import json, time, math
import JSONencoder as je
import pandas as pd
import copy
from bson import ObjectId

class NameMatching():

    def mapbox_routing(self,trip):
        '''
        This function implements the Directions Mapbox API.
        It takes as input the trip document and returns it with an enhanced
        set of coordinates.
        '''

        enhanced_trip = copy.deepcopy(trip) # copy original trip
        api_url = "https://api.mapbox.com/directions/v5/mapbox/cycling/" # url of API
        access_token = "pk.eyJ1IjoiZnJhYmFybG9jIiwiYSI6ImNrOWlqdnp5ajA4MWQzZ21nMDhkaTk2Y3gifQ.GXf8khH_0axh_HUwMKjUZw"
        max_len = 25 # max n points that the API can process in one single call
        radius_param = 25 # max distance (meters) that the points can be moved of

        geom_dict = enhanced_trip["tracepoints"] # extract points of the trip
        l = len(geom_dict["latitudes"]) # n of points in the trip
        n_calls = int(1+(l-1)//max_len) # n of calls to the API that need to be made
        print("Npoints original = ", l)
        routes = [] # store points matched to the map with relative streetname
        # loop over the different required calls (if points are too many for one call only)
        for n in range(n_calls):
            coord = "" # string with point coordinates to be put in the URL
            rad_param = "" # string with the radius parameters to be put in the URL
            # build the URL inserting all points in the trip that fits
            for k in range(min(l-n*max_len,max_len)-1):
                coord += str(geom_dict["longitudes"][k+max_len*n]) + "," + str(geom_dict["latitudes"][k+max_len*n]) + ";"
                rad_param += str(radius_param)+";" # use 25 meters as radius parameter, for all points
            if n<n_calls-1: # insert last coordinate of the payload
                coord += str(geom_dict["longitudes"][(n+1)*max_len-1]) + "," + str(geom_dict["latitudes"][(n+1)*max_len-1])
            else:
                coord += str(geom_dict["longitudes"][l-1]) + "," + str(geom_dict["latitudes"][l-1])
            rad_param += str(radius_param)
            # compose final URL string
            url = api_url + coord + "?geometries=geojson" + "&access_token=" + access_token
            # query API
            response = r.get(url)
            if not(response):
                print("No response from Mapbox Routing for trip:",str(enhanced_trip["_id"]))
                print(response.json())
            else: # append returned points to the list
                res_dict = response.json()
                if "routes" in res_dict:
                    routes+=res_dict["routes"][0]["geometry"]["coordinates"]
        if not(routes):
            return -1
        else:
            lon = []
            lat = []
            for current_coord in routes:
                lon.append(current_coord[0])
                lat.append(current_coord[1])

            enhanced_trip["tracepoints"]["longitudes"] = lon
            enhanced_trip["tracepoints"]["latitudes"] = lat

            return enhanced_trip


    def DistanceEvaluator(self,lat1, lon1, lat2, lon2):
        '''
        This function computes the distance in meters between two points given
        their coordinates (latitude, longitude).
        '''

        R = 6372800  # Earth radius in meters

        phi1, phi2 = math.radians(lat1), math.radians(lat2)
        dphi       = math.radians(lat2 - lat1)
        dlambda    = math.radians(lon2 - lon1)

        a = math.sin(dphi/2)**2 + \
            math.cos(phi1)*math.cos(phi2)*math.sin(dlambda/2)**2

        return 2*R*math.atan2(math.sqrt(a), math.sqrt(1 - a))


    def CoordinatesRetriever(self,lat1, lon1, lat2, lon2, q):
        '''
        This function computes q points in between two points given as input,
        placed along the straight line, equally spaced.
        It returns the list of such added points.
        '''

        lat_list = []
        lon_list = []

        for i in range(q):

            new_lat = round(((q-i)*lat1 + (i+1)*lat2)/(q+1), 6)
            new_lon = round(((q-i)*lon1 + (i+1)*lon2)/(q+1),6)
            new_coord = round(new_lat, 6), round(new_lon, 6)

            lat_list.append(new_lat)
            lon_list.append(new_lon)

        return (lat_list, lon_list)


    def AddCoordinates(self, enhanced_trip, max_distance):
        '''
        This function, given a trip as input and the maximum distance that
        we want to have between any two points, calls iteratively the function
        CoordinatesRetriever, adds these points to the trip itself and returns it.
        '''

        new_longitude_list = []
        new_latitude_list = []

        old_longitude_list = enhanced_trip["tracepoints"]["longitudes"]
        old_latitude_list = enhanced_trip["tracepoints"]["latitudes"]

        for i in range(len(old_longitude_list)-1):

            new_longitude_list.append(round(old_longitude_list[i],6))
            new_latitude_list.append(round(old_latitude_list[i], 6))

            # Evaluating the distance between the points,
            # if greater than max_distance, add new points
            dist = self.DistanceEvaluator(old_latitude_list[i], old_longitude_list[i], old_latitude_list[i+1], old_longitude_list[i+1])
            q = int(dist//max_distance)

            if (q >= 1):
                # crating new coordinates inside the two original ones
                new_lat, new_lon = self.CoordinatesRetriever(old_latitude_list[i], old_longitude_list[i], old_latitude_list[i+1], old_longitude_list[i+1], q)

                new_longitude_list += new_lon
                new_latitude_list += new_lat


        new_longitude_list.append(round(old_longitude_list[len(old_longitude_list)-1], 6))
        new_latitude_list.append(round(old_latitude_list[len(old_latitude_list)-1], 6))

        enhanced_trip["tracepoints"]["longitudes"] = new_longitude_list
        enhanced_trip["tracepoints"]["latitudes"] = new_latitude_list

        return enhanced_trip


    def mapbox_mapmatching(self,trip):

        '''
        This function implements the MapBox map-matching API.
        MapBox map-matching API documentation at: https://docs.mapbox.com/api/navigation/#map-matching

        INPUT:  trip = document retrieved from the trip collection in
                    MongoDB. It's a dictionary with 4 keys: "_id","type","properties" and
                    "tracepoints". Particularly, "tracepoints" is itself a dictionary with
                    2 keys, "latitudes" and "longitudes", both of which are lists and contain
                    the GPS coordinates of the points of the trip.

        OUTPUT: the function returns the response of the MapBox MapMatching API as
                a json string, which is actually a list of points, i.e. of dictionaries,
                each one having as keys:
                "location" = map matched coordinates,
                "name" = name of the street that the point has been matched to
                some others not used.
        '''

        # url address for the MapBox MapMatching API
        api_url = "https://api.mapbox.com/matching/v5/mapbox/driving/"
        # token for the MapBox API
        access_token = "pk.eyJ1IjoiZnJhYmFybG9jIiwiYSI6ImNrOWlqdnp5ajA4MWQzZ21nMDhkaTk2Y3gifQ.GXf8khH_0axh_HUwMKjUZw"
        max_len = 100 # max number of points that can be processed by the API
        radius_param = 25 # max distance (meters) that a point can be moved of in the matching

        ## PREPARE DATA TO QUERY MAPBOX
        geom_dict = trip["tracepoints"] # extract points of the trip
        l = len(geom_dict["latitudes"]) # n of points in the trip
        n_calls = int(1+(l-1)//max_len) # n of calls to the API that need to be made
        print("Npoints enhanced = ", l)
        tracepoints = [] # store points matched to the map with relative streetname
        # loop over the different required calls (if points are too many for one call only)
        for n in range(n_calls):
            coord = "" # string with point coordinates to be put in the URL
            rad_param = "" # string with the radius parameters to be put in the URL
            # build the URL inserting all points in the trip that fits
            for k in range(min(l-n*max_len,max_len)-1):
                coord += str(geom_dict["longitudes"][k+max_len*n]) + "," + str(geom_dict["latitudes"][k+max_len*n]) + ";"
                rad_param += str(radius_param)+";" # use 25 meters as radius parameter, for all points
            if n<n_calls-1: # insert last coordinate of the payload
                coord += str(geom_dict["longitudes"][(n+1)*max_len-1]) + "," + str(geom_dict["latitudes"][(n+1)*max_len-1])
            else:
                coord += str(geom_dict["longitudes"][l-1]) + "," + str(geom_dict["latitudes"][l-1])
            rad_param += str(radius_param)
            # compose final URL string
            url = api_url + coord + "?radiuses=" + rad_param + "&access_token=" + access_token
            # query API
            response = r.get(url)
            if not(response):
                print("No response from Mapbox Map Matching for trip:",str(trip["_id"]))
                print(response.json())
            else: # append returned points to the list
                res_dict = response.json()
                if "tracepoints" in res_dict:
                    tracepoints+=res_dict["tracepoints"]
        if not(tracepoints):
            return -1
        else:
            return tracepoints


    def match_path_by_name(self,trip_segment_name,cyclepaths_data):

        '''
        This functions finds, for a trip, the matches by name in the cyclepath
        collection.

        INPUT:  trip_segment_name : name of all points in the segment. (e.g.
                    "Corso Vittorio Emanuele II")
                cyclepaths_data : dictionary containing all cyclepath documents

        OUTPUT: list with the ID's of the cyclepaths whose names matched with the streetname
                    of the trip. '''

        id_match_list = []
        tripname_split = trip_segment_name.upper().split(" ")
        # iterate over all cycle paths
        for path in cyclepaths_data:
            # if a match on the streetname is found, store that ID
            pathname_split = path["properties"]["street_name"].split(" ")
            all_pathname_in_tripname = all(elem in tripname_split for elem in pathname_split)
            all_tripname_in_pathname = all(elem in pathname_split for elem in tripname_split)

            if all_pathname_in_tripname or all_tripname_in_pathname:
                id_match_list.append(path["_id"])
        return id_match_list


    def out_json(self,trip_id,cyclepath_id,tripDate,startTime,endTime):
        '''
        This function builds the json that has to be inserted in the output collection:
        It contains the id of the trip and of the path that the trip has been assigned to.
        It also contains information on date and time of the trip.

        INPUT:  trip_id = id of the trip;
                cyclepath_id = id of the cyclepath;
                tripDate, startTime, endTime = time info on the trip

        OUTPUT: json to be inserted in the output collection
        '''

        jencoder = je.JSONEncoder()
        #cyclepath_id =jencoder.default(cyclepath_id)
        trip_id =jencoder.default(trip_id)
        x =  {
                "cyclepath_id" :cyclepath_id,
                "tripID" : trip_id,
                "tripDate" : tripDate,
                "startTime" :startTime,
                "endTime" : endTime
            }
        y = json.dumps(x)
        return y


    def verify_match_by_coordinates(self, id_list, cyclepaths_data, data_trip_segment, trip_data):

        '''
        This function verifies if points that have been matched by name to a cyclepath actually lie on
        it or are outside. An ideal rectangle identified by the extreme coordinates of the cyclepath is
        created and the position inside/outside of each trip point is checked.
        For a given trip street name, there can be up to two matches: one with the the
        "existing" cyclepath and one with the "unexisting" one (which is the cyclepath made of points of previous
        trips passing on the street, but on a portion of it which is not covered by the existing cycle path.

        If only the "existing" cyclepath is present, points outside of it will be used to create an "unexisting"
        cyclepath in the database.
        If only the "unexisting" cyclepath is present, all points of the new trip will be added to it.
        In case both are present, all points not belonging to the "existing" cyclepath are added to the "unexisting"
        one.

        INPUT:  id_list = list of the cyclepaths id's that have been matched to the trip segment (can be 1 or 2).
                cyclepaths_data = list of all cyclepath documents
                data_trip_segment = map-matched coordinates of the trip_segment
                trip_data = original trip document (contains info such as street_name)

        OUTPUT: matched_segments_json = list of json documents to be added to the output collection
                                        can be 0,1,2 - depending on whether there are points
                                        matched to the "existing" and/or "unexisting" cyclepath.
                matched_coordinates_list = list of lists of points matched to the "existing" and/or "unexisting" cyclepath:
                                            all points not on the "existing" path are assigned to the "unexisting" one, if
                                            present.
                unmatched_coordinates_list = list of points to be used to create a new cyclepath (only if
                                            the "unexisting" cyclepath on that street has not been built yet).
        '''

        matched_existent = [] # list of points matched to the existing path
        matched_unexistent = [] # list of points matched to the unexisting path
        tobuild_unexistent = [] # list of points not matched with any path
        path_exist = {} # "existing" cyclepath
        path_unexist = {} # "unexisting" cyclepath

        # assign "existing" and/or "unexisting" cyclepath based on input trip id (id_list)
        for id in id_list:
            for p in cyclepaths_data:
                if isinstance(p["_id"], ObjectId):
                    if str(p["_id"])==str(id):
                        if (p["existing"]):
                            path_exist = copy.deepcopy(p)
                        else:
                            path_unexist = copy.deepcopy(p)

        # iterate over all points and assign each one either to the "existing" path,
        # or the "unexisting" one (if the unexisting is not present and there are
        # points outside the "existing" one, these points will be used to create the
        # "unexisting" one)
        for point in data_trip_segment:
            # if the existing path is present, check if the point is on it
            if path_exist:
                match_flag = False

                # for all segments that the cyclepath is divided into, check if the point
                # is inside the ideal rectangle made of the extreme coordinates of the cyclepath segment
                for i in range(len(path_exist["geometry"]["latitudes"])):
                    # find extreme coordinates
                    lat_first = path_exist["geometry"]["latitudes"][i][0]
                    lat_last = path_exist["geometry"]["latitudes"][i][len(path_exist["geometry"]["latitudes"][i])-1]
                    lon_first = path_exist["geometry"]["longitudes"][i][0]
                    lon_last = path_exist["geometry"]["longitudes"][i][len(path_exist["geometry"]["latitudes"][i])-1]
                    # find rectangle vertices
                    lat_min = min(lat_first, lat_last)
                    lat_max = max(lat_first, lat_last)
                    lon_min = min(lon_first, lon_last)
                    lon_max = max(lon_first, lon_last)
                    # check if point is inside rectangle
                    if ((point[0]>lon_min and point[0]<lon_max) and (point[1]>lat_min and point[1]<lat_max)):
                        match_flag = True

                if match_flag: # if inside
                    matched_existent.append(tuple(point))
                elif path_unexist: # if outside and there is "unexisting" path
                    matched_unexistent.append(tuple(point))
                else: # if outside and there is no "unexisting" path
                    tobuild_unexistent.append(tuple(point))

            # if the existent is not present, only the unexisting path is present
            # and all points are assigned to it
            else:
                matched_unexistent.append(tuple(point))

        # lists to be returned:
        matched_segments_json = []
        matched_coordinates_list = []
        unmatched_coordinates_list = []

        # if both "existing" and "unexisting" are present
        if(path_exist and path_unexist):
            print(len(matched_existent),"points on existing path.")
            print(len(matched_unexistent),"points on unexisting path.")
            if matched_existent and len(matched_existent)>1: # if there are points on "existent": build json, append coordinates
                output_existent = self.out_json(trip_data["_id"],path_exist["properties"]["ID"],trip_data["properties"]["date"],
                trip_data["properties"]["startTime"],trip_data["properties"]["endTime"])
                matched_segments_json.append(output_existent)
                matched_coordinates_list.append({path_exist["properties"]["ID"]:matched_existent})
            if matched_unexistent and len(matched_unexistent)>1: # if there are points on "unexistent": build json, append coordinates
                output_unexistent = self.out_json(trip_data["_id"],path_unexist["properties"]["ID"],trip_data["properties"]["date"],
                trip_data["properties"]["startTime"],trip_data["properties"]["endTime"])
                matched_segments_json.append(output_unexistent)
                matched_coordinates_list.append({path_unexist["properties"]["ID"]:matched_unexistent})
        # if only "unexisting" is present
        elif(path_exist):
            print(len(matched_existent),"points on existing path.")
            if matched_existent and len(matched_existent)>1: # if there are points on "existent": build json, append coordinates
                output_existent = self.out_json(trip_data["_id"],path_exist["properties"]["ID"],trip_data["properties"]["date"],
                trip_data["properties"]["startTime"],trip_data["properties"]["endTime"])
                matched_segments_json.append(output_existent)
                matched_coordinates_list.append({path_exist["properties"]["ID"]:matched_existent})
            if tobuild_unexistent and len(tobuild_unexistent)>1: # if there are points to be used to build new "unexisting" path, append them
                unmatched_coordinates_list.append({path_exist["properties"]["street_name"]:tobuild_unexistent})
                print("Creating new path with",len(tobuild_unexistent),"points.")
        # if only "unexisting" is present
        elif(path_unexist):
            print(len(matched_unexistent),"points on unexisting path.")
            if matched_unexistent and len(matched_unexistent)>1: # if there are points on "unexistent": build json, append coordinates
                output_unexistent = self.out_json(trip_data["_id"],path_unexist["properties"]["ID"],trip_data["properties"]["date"],
                trip_data["properties"]["startTime"],trip_data["properties"]["endTime"])
                matched_segments_json.append(output_unexistent)
                matched_coordinates_list.append({path_unexist["properties"]["ID"]:matched_unexistent})

        return matched_segments_json, matched_coordinates_list, unmatched_coordinates_list


    def run(self, trip_data, cyclepaths_data):

        '''
        This function executes the whole NameMatching program.

        INPUT:  trip_data = the trip to analyse
                cyclepaths_data = list of all cyclepaths

        OUTPUT: matched_segments_json = list of jsons to be added to the output collection

                matched_coordinates_list = list of dictionaries. Each dictionary
                in the list has a unique key, which is the numeric id of the cycle path that
                the points were associated to, and as value a list of
                tuples (latitude, longitude), which are the points associated to an already
                defined cycle path (be it existing or not).

                unmatched_coordinates_list = list of dictionaries. Each dictionary
                in the list has a unique key, which is the street name that the
                points were matched to. For each dictionary, a new cycle path
                will be created.

                flag_failed = a flag which is true iff the program failed.

        '''

        print("Performing name matching analysis...")
        matched_segments_json = [] # json to be written to output collection
        matched_coordinates_list = [] # list to be used to export data to csv file
        not_matched_coordinates_list = [] # list to be used to write points to cyclepaths and csv file
        flag_failed = False # flag to decide if at the end distance_matching is needed
        max_distance = 1000 # max distance between coordinates (in meters)

        enhanced_trip_data = self.mapbox_routing(trip_data)

        enhanced_trip_data = self.AddCoordinates(enhanced_trip_data, max_distance)

        mapped_data = self.mapbox_mapmatching(enhanced_trip_data)

        # if invalid data or no answer from MapBox, set flag and exit
        if mapped_data == -1:
            flag_failed = True
            print("mapbox returned -1.")
        else:
            # count points that were not matched by mapbox
            cnt_none=0
            mapped_data_filtered=[]
            for point in mapped_data:
                if not(point):
                    cnt_none+=1
                else:
                    mapped_data_filtered.append(point)
            # if they are more than 20%, consider the analysis failed
            if cnt_none > 0.5*len(mapped_data):
                flag_failed=True
                print("N of not-map-matched-points above threshold:",cnt_none/len(mapped_data)*100,"%.")
            # otherwise, group points into segments by street name
            else:
                df = pd.DataFrame(mapped_data_filtered)
                trip_segments_df = df.groupby('name')['location'].apply(list).reset_index(name='locationtrace')

        # if there were no errors
        if not(flag_failed):
            # iterate over trip segments
            for i in range(len(trip_segments_df)):
                # if a segment has no street name, skip it
                if not(trip_segments_df["name"][i]):
                    print("\n"+str(i+1)+") no name... skipping...")
                    continue
                print("\n"+str(i+1)+")",trip_segments_df["name"][i])
                if len(trip_segments_df['locationtrace'][i])==1:
                    print("Segment contains just one point. Skip it.")
                    continue
                # assign cyclepath(s) to the segment
                cyclepath_id = self.match_path_by_name(trip_segments_df['name'][i],cyclepaths_data)
                # if no match is found, the segment will be added as a new cyclepath
                if not(cyclepath_id):
                    not_matched_coordinates_list.append({trip_segments_df['name'][i].upper():trip_segments_df["locationtrace"][i]})
                    print("New path created with", str(len(trip_segments_df["locationtrace"][i])),"points.")
                # if a match is found, verify if points actually belong to it
                else:
                    msj,mcl,ucl = self.verify_match_by_coordinates(cyclepath_id, cyclepaths_data, trip_segments_df["locationtrace"][i],trip_data)
                    for mj in msj:
                        matched_segments_json.append(mj)
                    for ml in mcl:
                        matched_coordinates_list.append(ml)
                    for ul in ucl:
                        not_matched_coordinates_list.append(ul)

        if flag_failed:
            print("\nName matching analysis failed.\n")
        else:
            print("\nName matching analysis complete.")
            print(len(matched_coordinates_list+not_matched_coordinates_list),"different segments identified:")
            print(len(matched_coordinates_list), "assigned to an existing cyclepath;")
            print(len(not_matched_coordinates_list), "to be added as new cyclepaths.\n")


        return enhanced_trip_data, matched_segments_json, matched_coordinates_list, not_matched_coordinates_list, flag_failed
