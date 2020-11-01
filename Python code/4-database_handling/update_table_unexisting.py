#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Created on Tue Mar 17 23:50:44 2020
@author: jijo
"""
class update_unexisting():
    def update_unexisting(self,db):

        '''
        This function is used to update the tag from "new" to "old"
        of a newly created cyclepath in the database.
        '''

        res = db.cycle_path_data.find({"tag":"new"})
        n = db.cycle_path_data.find({"tag":"new"}).count()
        if n>0:
            for i in res:
                result = db.cycle_path_data.update_one({'_id': i["_id"]},{"$set":{"tag":"old"}})
            msg = str(n)+" document(s) updated in the cyclepath collection."
        else:
            msg = "No cyclepaths with tag='new' to be updated."
        return msg
