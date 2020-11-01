#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Created on Wed Mar 18 13:23:37 2020
@author: Francesco
"""
import numpy as np
import os
import glob
import os, shutil

class ExportToCSV():

  def export_to_csv(trip_data,enhanced_trip_data,list_exist,list_unexist):

      # empty folder with matched segments csv
      folder = '../data/csv_repository/matched_segments'
      for filename in os.listdir(folder):
          file_path = os.path.join(folder, filename)
          if os.path.isfile(file_path) or os.path.islink(file_path):
              os.unlink(file_path)

      # empty folder with unmatched segments csv
      folder = '../data/csv_repository/unmatched_segments'
      for filename in os.listdir(folder):
          file_path = os.path.join(folder, filename)
          if os.path.isfile(file_path) or os.path.islink(file_path):
              os.unlink(file_path)

      # save csv with original coordinates of the trip
      my_header="longitudes,latitudes"
      l=len(trip_data["tracepoints"]["latitudes"])
      coordinates = np.zeros((l,2))
      for i in range(l):
          coordinates[i][0] = trip_data["tracepoints"]["longitudes"][i]
          coordinates[i][1] = trip_data["tracepoints"]["latitudes"][i]
          np.savetxt("../data/csv_repository/generated_points/original_coordinates.csv", coordinates, delimiter=",",header=my_header)

      # save csv with enhanced coordinates of the trip
      my_header="longitudes,latitudes"
      l=len(enhanced_trip_data["tracepoints"]["latitudes"])
      coordinates = np.zeros((l,2))
      for i in range(l):
          coordinates[i][0] = enhanced_trip_data["tracepoints"]["longitudes"][i]
          coordinates[i][1] = enhanced_trip_data["tracepoints"]["latitudes"][i]
          np.savetxt("../data/csv_repository/generated_points/enhanced_coordinates.csv", coordinates, delimiter=",",header=my_header)

      # save each matched segment in a specific csv
      s=1
      for item in list_exist:
          for k in item.keys():
              id_key=k
          coordinates = np.zeros((len(item[id_key]), 2))
          for i in range(len(item[id_key])):
              coordinates[i][0] = item[id_key][i][0]
              coordinates[i][1] = item[id_key][i][1]
          np.savetxt("../data/csv_repository/matched_segments/segment_"+str(s)+".csv", coordinates, delimiter=",",header=my_header)
          s+=1

      # save each unmatched segment in a specific csv
      s=1
      for item in list_unexist:
          for k in item.keys():
              name_key=k
          coordinates = np.zeros((len(item[name_key]), 2))
          for i in range(len(item[name_key])):
              coordinates[i][0] = item[name_key][i][0]
              coordinates[i][1] = item[name_key][i][1]
          np.savetxt("../data/csv_repository/unmatched_segments/segment_"+str(s)+".csv", coordinates, delimiter=",",header=my_header)
          s+=1
