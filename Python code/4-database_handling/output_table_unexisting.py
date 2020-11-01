#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Created on Tue Mar 17 12:45:11 2020
@author: jijo
"""
import json
import JSONencoder as je

class output_unexisting():

   def output_unexisting_json(self,trip_id,cyplepath_id,tripDate,startTime,endTime):

       '''
       This function is used to write trip segments not matched to any existing cyclepath to output collection.
       '''

       jencoder = je.JSONEncoder()
       trip_id =jencoder.default(trip_id)
       cyplepath_id =jencoder.default(cyplepath_id)
       x =  {
               "cyclepath_id" :cyplepath_id,
               "tripID" : trip_id,
               "tripDate" : tripDate,
               "startTime" :startTime,
               "endTime" : endTime
        }
       y = json.dumps(x)
       return y


   def unexisting(self,db):
       cycleid = []
       res = db.cycle_path_data.find({"tag":"new"})
       for i in res:
           cycleid.append(i["properties"]["ID"])
       return cycleid


   def output_unexisting(self,trip_data,db):
       output_json = []
       cycleid = self.unexisting(db)
       if not(cycleid):
           msg = "3) No trips with unmatched data to be updated in the output collection."
           return None
       else:
           for i in cycleid:
               output = self.output_unexisting_json(trip_data["_id"],i,trip_data['properties']["date"],trip_data["properties"]["startTime"],trip_data["properties"]["endTime"])
               output_json.append(output)
           return output_json
