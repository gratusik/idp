#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Created on Tue Mar 17 10:40:45 2020
@author: jijo
"""

# import mapbox
import json
import requests as r
import copy

class input_table_unexisting():

    def input_unexisting(self,db,cyclepaths_data,data_list):

        '''
        This function is used to add new cyclepaths to the cyclepath collection.
        '''
        cycle_to_be_added_flag = bool(data_list)
        # if there are no cyclepaths to be added, exit
        if not(cycle_to_be_added_flag):
            msg = "No new cyclepaths to be added to the collection."
        # otherwise, compute the ID to assign to the first path
        # (the minimum number not present in the collection)
        else:
            id_max_used=0
            for path in cyclepaths_data:
                if path["properties"]["ID"]>id_max_used:
                    id_max_used=path["properties"]["ID"]

            # skeleton of the document to be added to the collection
            doc_default = {
                "type":"path",
                "properties":{
                    "ID":0,
                    "city":"TORINO",
                    "street_name":"",
                    "length":0},
                "existing": False,
                "tag": "new",
                "geometry":{
                    "type":"tracepoints",
                    "latitudes":[],
                    "longitudes":[]}}

        i=1
        for item in data_list:
            doc=copy.deepcopy(doc_default)
            lat_list=[]
            long_list=[]
            for k in item.keys():
                key=k
            for point in item[key]:
                long_list.append(point[0])
                lat_list.append(point[1])
            doc["geometry"]["latitudes"].append(lat_list)
            doc["geometry"]["longitudes"].append(long_list)
            doc["properties"]["ID"]=id_max_used+i
            doc["properties"]["street_name"]=key
            result = db.cycle_path_data.insert_one(doc)
            i+=1 # increment for next ID
        msg = str(len(data_list))+" documents(s) added to the cyclepath collection."

        return msg,cycle_to_be_added_flag
