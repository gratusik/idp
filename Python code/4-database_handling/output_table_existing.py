#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Created on Tue Mar 10 03:51:31 2020
@author: jijo
"""
import json
class output_existing():
    def output_existing(self,db,output_json):

        '''
        This function is used to insert a json in the output collection.
        '''

        if not(output_json):
            msg = "No documents to be added to the output collection."
            return msg
        else:
            for i in output_json:
                file_data = json.loads(i)
                result = db.output.insert_one(file_data)
            msg = str(len(output_json)) + " document(s) added to the output collection."
            return msg
