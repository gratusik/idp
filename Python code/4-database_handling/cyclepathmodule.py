#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Created on Fri Mar  6 23:05:02 2020
@author: jijo
"""

class cyclepathmodule():
    def cycledata(db):

        '''
        This function is used to retrieve the list of all cyclepaths.
        '''

        cycledata = []
        res = db.cycle_path_data.find({})
        for  i in res:
            cycledata.append(i)
        return cycledata
