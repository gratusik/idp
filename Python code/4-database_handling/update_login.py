#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Created on Fri Apr 24 19:50:44 2020
@author: Francesco
"""

import json
from bson.json_util import dumps
import copy

class update_login():
    def update_login(self,db):

        '''
        This function is used to set the "update" field (for all users,
        in the login collection) to True whenever the cycle path collection
        has been modified in some way (adding or modifying one or more paths).
        '''

        # export the json of the cyclepath collection
        path_to_json = "../data/cycle_paths"
        cyclepaths_list = []
        cyclepaths_crs = db.cycle_path_data.find({})
        n_cyclepaths = db.cycle_path_data.find({}).count()

        for p in cyclepaths_crs:
            path=copy.deepcopy(p)
            #path["_id"]=str(path["_id"])
            cyclepaths_list.append(path)
        #cyclepaths_json_file = json.dumps(cyclepaths_list)
        cyclepaths_json = dumps(cyclepaths_list)

        with open('../data/cycle_paths/cycle_path_data.json', 'w') as outfile:
            outfile.write(cyclepaths_json)

        res_log = db.login.find({})
        n_log = db.login.find({}).count()
        if n_log>0:
            for doc in res_log:
                result = db.login.update_one({'_id': doc["_id"]},{"$set":{"update":True}})
                result = db.login.update_one({'_id': doc["_id"]},{"$set":{"folder_path":path_to_json}})
            msg = str(n_log)+" documents updated in the login collection."
        else:
            msg = "No users are registered in the database."
        return msg
