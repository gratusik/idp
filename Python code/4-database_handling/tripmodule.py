#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Created on Fri Mar  6 22:42:27 2020
@author: jijo
"""

class tripmodule():
    def tripdata(db):

        '''
        This function is used to retrieve one single trip from the database.
        '''

        #tripdata = db.app_trip_data.find_one()
        trip_cursor = db.app_trip_data.aggregate([{ "$sample": { "size": 1 } }])
        #trip_cursor = db.synthetic_trip_data.aggregate([{ "$sample": { "size": 1 } }])
        for tripdata in trip_cursor:
            return tripdata
