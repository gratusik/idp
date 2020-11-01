#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Created on Wed Mar 11 12:11:40 2020
@author: Ste
"""

import numpy as np
import random
import time
from selenium import webdriver
from webdriver_manager.chrome import ChromeDriverManager
from selenium.webdriver.common.by import By
from selenium.webdriver.common.keys import Keys
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support.expected_conditions import presence_of_element_located
from gpx_csv_converter import Converter
import os.path
import pandas as pd
import gpx_parser as parser
from datetime import date
from datetime import datetime
import json
import math
from shapely.geometry import LineString
from geographiclib.geodesic import Geodesic
import csv
import sys; sys.path.append("../main")
import config as cf
import shutil

class TripGenerator():

    '''
    This class contains functions to create simulated trips and add them to
    the database.
    '''

    def Website_queries(self):

        driver = webdriver.Chrome(executable_path=os.path.abspath("../chromedriver"))

        # latitude and longitude of Turin, to be divided by 10000
        long_min = 76170
        long_max = 76853
        lat_min = 450340
        lat_max = 450779

        # extract random source and destination of the trip
        long_start = random.randrange(long_min, long_max)/10000
        long_end = random.randrange(long_min, long_max)/10000
        lat_start = random.randrange(lat_min, lat_max)/10000
        lat_end = random.randrange(lat_min, lat_max)/10000

        # GET to create the trip
        driver.get('https://graphhopper.com/maps/?point='+str(lat_start)+'%2C'+str(long_start)+'&point='+str(lat_end)+'%2C'+str(long_end)+'&locale=it-IT&vehicle=bike&weighting=fastest&elevation=true&turn_costs=false&use_miles=false&layer=Omniscale')

        button_element = driver.find_element_by_xpath("/html/body/div[1]/div[1]/div[4]/a/img")
        button_element.click()

        export_element = driver.find_element_by_xpath("/html/body/div[3]/div[3]/div/button[1]")
        export_element.click()

        time.sleep(2)

        return


    def DistanceEvaluator(self,lat1, lon1, lat2, lon2):
        R = 6372800  # Earth radius in meters

        phi1, phi2 = math.radians(lat1), math.radians(lat2)
        dphi       = math.radians(lat2 - lat1)
        dlambda    = math.radians(lon2 - lon1)

        a = math.sin(dphi/2)**2 + \
            math.cos(phi1)*math.cos(phi2)*math.sin(dlambda/2)**2

        return 2*R*math.atan2(math.sqrt(a), math.sqrt(1 - a))


    def CoordinatesRetriever(self,lat1, lon1, lat2, lon2, q):
        lat_list = []
        lon_list = []

        for i in range(q):

            new_lat = round(((q-i)*lat1 + (i+1)*lat2)/(q+1), 6)
            new_lon = round(((q-i)*lon1 + (i+1)*lon2)/(q+1),6)
            new_coord = round(new_lat, 6), round(new_lon, 6)

            lat_list.append(new_lat)
            lon_list.append(new_lon)

        return (lat_list, lon_list)


    def Noise_adder(self,lat_list, long_list, meters):

        dlat = 0.000009
        dlong = 0.0000129
        a = meters*dlong
        b = meters*dlat

        for i in range(len(lat_list)):

            delta_lat = np.random.normal(0, b)
            delta_long = np.random.normal(0, a)

            lat_list[i] += delta_lat
            long_list[i] += delta_long
            lat_list[i] = round(lat_list[i],6)
            long_list[i] = round(long_list[i],6)

        return lat_list, long_list


    def trip_creation(self,file_name):
        max_distance = 100 # max distance between the points (in meters)
        noise = 20 # noise introduced to have more realistic data (in meters)

        folder = os.path.expanduser("~/Downloads")

        with open(folder+'/'+file_name, 'r') as gpx:
            file = parser.parse(gpx)
            gpx.close()
        os.remove(folder+'/'+file_name)

        lat=[]
        long=[]
        time=[]

        # retrieve information by the gpx file
        for track in file.tracks:
            for segment in track.segments:
                for point in segment.points:
                    lat.append(point.latitude)
                    long.append(point.longitude)
                    try:
                        time_string = point.time.strftime("%d-%m-%Y %H:%M:%S")
                        time.append(time_string)
                    except:
                        pass

        date_hour = []
        for i in range(len(time)):
            date_hour.append(time[i].split(" "))

        longitude_list = []
        latitude_list = []

        for i in range(len(lat)-1):

            longitude_list.append(round(long[i],6))
            latitude_list.append(round(lat[i], 6))

            # Evaluating the distance between the points,
            # if greater than max_distance, add new points
            dist = self.DistanceEvaluator(lat[i], long[i], lat[i+1], long[i+1])
            q = int(dist//max_distance)

            if (q >= 1):
                # crating new coordinates inside the two original ones
                new_lat, new_lon = self.CoordinatesRetriever(lat[i], long[i], lat[i+1], long[i+1], q)

                longitude_list += new_lon
                latitude_list += new_lat


        longitude_list.append(round(long[len(long)-1], 6))
        latitude_list.append(round(lat[len(lat)-1], 6))

        coord = np.zeros((len(latitude_list), 2))

        for i in range(len(latitude_list)):
            coord[i][0] = latitude_list[i]
            coord[i][1] = longitude_list[i]

        # adding noise to coordinates
        latitude_list, longitude_list = self.Noise_adder(latitude_list, longitude_list, noise)
        coord_noise = np.zeros((len(latitude_list), 2))

        for i in range(len(latitude_list)):
            coord_noise[i][0] = latitude_list[i]
            coord_noise[i][1] = longitude_list[i]

        # creation of the dictionary that uncludes the current trip
        trip = {
            "type" : "trip",
            "properties" : {
                "detectedMode" : "Bicycle",
                "date" : date_hour[0][0],
                "startTime" : date_hour[0][1],
                "endTime" : date_hour[len(date_hour)-1][1]
            },
            "tracepoints" : {
                "latitudes" : latitude_list,
                "longitudes" : longitude_list,
            }
        }

        return trip


    def run(self,n_trips):
        mydb = cf.config.opendb()
        file_name = "GraphHopper.gpx"
        t=0
        while(t<n_trips):
            self.Website_queries()
            trip_doc = self.trip_creation(file_name)
            mydb.synthetic_trip_data.insert_one(trip_doc)

            time.sleep(1)
            t+=1


if __name__ == '__main__':

    if len(sys.argv)>1:
    	n_trips = int(sys.argv[1])
    else:
        n_trips = 1
    my_gen = TripGenerator()
    my_gen.run(n_trips)
