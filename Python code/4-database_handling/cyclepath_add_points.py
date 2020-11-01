#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Created on Tue Mar 10 03:51:31 2020
@author: Francesco
"""
import json
from bson import ObjectId
import copy

class cyclepath_add_points():
    def cyclepath_add_points(self,db,cycledata,json_exist,list_exist):

        '''
        This function is used to insert coordinates of a new trip to an
        "unexisting" cyclepath.
        '''

        max_id_exist=204
        n=len(list_exist)
        cnt_upd=0
        if n==0:
            msg = "No documents to be added to the output collection."
            return msg,cnt_upd
        else:
            for i in range(n):
                for k in list_exist[i].keys():
                    key=k

                for p in cycledata:
                    if p["properties"]["ID"]==key:
                            path=copy.deepcopy(p)

                if key>max_id_exist:
                    lat_list=[]
                    long_list=[]
                    for point in list_exist[i][key]:
                        lat_list.append(point[1])
                        long_list.append(point[0])
                    path["geometry"]["latitudes"].append(lat_list)
                    path["geometry"]["longitudes"].append(long_list)
                    cnt_upd+=1
                    result = db.cycle_path_data.replace_one({"properties.ID":key},path)

            msg = str(cnt_upd) + " document(s) updated in the cyclepath collection."

            return msg, cnt_upd
