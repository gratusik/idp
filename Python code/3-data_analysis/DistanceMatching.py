#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Created on Wed Mar 18 13:23:37 2020
@author: jijo
"""

from math import radians, cos, sin, asin, sqrt
from geopy.distance import geodesic
import json
import JSONencoder as je
class DistanceMatching:

    def haversine(self,lon1, lat1, lon2, lat2):
        """
        Calculate the great circle distance between two points
        on the earth (specified in decimal degrees)
        """
        # convert decimal degrees to radians
        lon1, lat1, lon2, lat2 = map(radians, [lon1, lat1, lon2, lat2])

        # haversine formula
        dlon = lon2 - lon1
        dlat = lat2 - lat1
        a = sin(dlat/2)**2 + cos(lat1) * cos(lat2) * sin(dlon/2)**2
        c = 2 * asin(sqrt(a))
        r = 6371 # Radius of earth in kilometers. Use 3956 for miles
        return c * r

    def out_json(self,trip_id,cyplepath_id,tripDate,startTime,endTime):
        jencoder = je.JSONEncoder()
        cyplepath_id =jencoder.default(cyplepath_id)
        trip_id =jencoder.default(trip_id)
        x =  {
                "cyclepath_id" :cyplepath_id ,
                "tripID" : trip_id,
                "tripDate" : tripDate,
                "startTime" :startTime,
                "endTime" : endTime
            }
        y = json.dumps(x)
        return y


    def getdata(self,trip_data,exist_un_cyclepath_data):
        print("Performing distance matching analysis...")
        #in_count = 0
        in_count_cycle_id = []
        in_count = []
        out_count = []
        radius = 50 # in meters
        insertedLatLong = []
        triplatlong = []
        output_json = []
        trip_l_l = trip_data['tracepoints']
        trip_lat_long=  list(zip(trip_l_l['latitudes'],trip_l_l['longitudes']))
        for single_trip_lat_long in trip_lat_long:
            for single_path in exist_un_cyclepath_data:
                for i in range(len(single_path['geometry']['latitudes'])):
                    lat_list = single_path['geometry']['latitudes'][i]
                    long_list = single_path['geometry']['longitudes'][i]
                    for ii in range(len(lat_list)):
                        cycle_lat = lat_list[ii]
                        cycle_long = long_list[ii]
                        coords_1 = ( cycle_lat, cycle_long)
                        coords_2 = ( single_trip_lat_long[0], single_trip_lat_long[1])
                        a = geodesic(coords_1, coords_2).meters
                        if a <= radius:
                            if single_trip_lat_long not in in_count:
                                in_count.append(single_trip_lat_long)
                            if single_path["_id"] not in [j["_id"] for j in in_count_cycle_id] and single_trip_lat_long not in insertedLatLong:
                                insertedLatLong.append(single_trip_lat_long)
                                triplatlong.append(single_trip_lat_long)
                                in_count_cycle_id.append(single_path)
                                output = self.out_json(trip_data["_id"],single_path["properties"]["ID"],trip_data['properties']["date"],trip_data["properties"]["startTime"],trip_data["properties"]["endTime"])
                                output_json.append(output)
        for single_trip_lat_long in trip_lat_long:
            if single_trip_lat_long not in in_count:
                out_count.append(single_trip_lat_long)
        print("\nDistance matching analysis complete.")
        print(len(in_count),"points matched.")
        print(len(out_count),"points not matched.")
        return in_count,out_count,output_json
