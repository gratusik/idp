#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Created on Fri Mar 20 22:39:57 2020
@author: jijo
"""

import sys; import json
sys.path.append("../3-data_analysis")
sys.path.append("../4-database_handling")
import config as cf
import tripmodule as tp
import cyclepathmodule  as cpm
import NameMatching as nm
import DistanceMatching as dm
import output_table_existing as out_existing
import cyclepath_add_points as cycle_add
import input_table_unexisting as in_unexisting
import output_table_unexisting as out_unexisting
import update_table_unexisting as up_unexisting
import update_login
import export_to_csv as exp_csv

def table(db,exist_un_cyclepath_data,trip_data,json_exist,list_exist,list_unexist):

    '''
    This function implements all the operations related to the update of
    the database after a trip has been analysed.

    INPUT:  exist_un_cyclepath_data = list of all cyclepaths
            trip_data = the trip to be analysed
            json_exist = list of jsons to be added to the output collection
            list_exist = list of lists of coordinates (of different segments) matched to a cyclepath
            list_unexist = list of lists of coordinates (of different segments) to be used to create new paths
    '''

    # write trip segments matched to an existing cyclepath to output collection (1)
    out_table_existing = out_existing.output_existing()
    message = out_table_existing.output_existing(db,json_exist)
    print("\n1)",message)

    # write the matched trip data to the cyclepath collection if cyclepath["existing"]==False (1b)
    cycle_add_obj = cycle_add.cyclepath_add_points()
    message, cycle_updated_cnt = cycle_add_obj.cyclepath_add_points(db,exist_un_cyclepath_data,json_exist,list_exist)
    print("1b)",message)

    # write the not-matched trip data to the cyclepath collection as a new cyclepath (2)
    in_table_unexisting = in_unexisting.input_table_unexisting()
    message,cycle_added_flag = in_table_unexisting.input_unexisting(db,exist_un_cyclepath_data,list_unexist)
    print("2)",message)

    # write trip segments NOT matched to any existing cyclepath to output collection (3)
    out_table_unexisting = out_unexisting.output_unexisting()
    output_json_unexisting = out_table_unexisting.output_unexisting(trip_data,db)
    # print(output_json_unexisting)
    if output_json_unexisting:
        message = out_table_existing.output_existing(db,output_json_unexisting)
        print("3)",message)

        # in the cyclepaths collection, set to "old" the tag of the new cyclepaths added in this run (4)
        update_table_existing = up_unexisting.update_unexisting()
        message = update_table_existing.update_unexisting(db)
        print("4)",message)

    if cycle_updated_cnt or cycle_added_flag:
        up_log_obj = update_login.update_login()
        message = up_log_obj.update_login(db)
        print("5)",message)


if __name__ == '__main__':

    db = cf.config.opendb() # connect to the database

    #trip_cursor = db.synthetic_trip_data.find({}) # fetch all synthetic trips
    trip_cursor = db.app_trip_data.find({}) # fetch all app trips


    # run the analysis on all trips
    for trip_data in trip_cursor:
        # fetch all cyclepaths
        exist_un_cyclepath_data = cpm.cyclepathmodule.cycledata(db)

        # run the NameMatching algorithm
        nm_obj = nm.NameMatching()
        enhanced_trip_data,json_exist,list_exist,list_unexist,nm_failed_flag=nm_obj.run(trip_data,exist_un_cyclepath_data)

        # if NameMatching fails, run DistanceMatching
        if nm_failed_flag:
            distanceObject = dm.DistanceMatching()
            in_count_cycle_id,list_unexist,json_exist = distanceObject.getdata(trip_data,exist_un_cyclepath_data)
            # these two lists are set to empty so that if distance matching is run, no cycle paths will be created or updated
            # simply, the associations with already defined cycle paths will be stored in the output collection
            list_exist=[]
            list_unexist=[]
        # call function to do all the database work
        table(db,exist_un_cyclepath_data,enhanced_trip_data,json_exist,list_exist,list_unexist)
        # export coordinates to csv for visualization in QGIS
        exp_csv.ExportToCSV.export_to_csv(trip_data,enhanced_trip_data,list_exist,list_unexist)
