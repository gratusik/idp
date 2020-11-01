#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Created on Fri Mar  6 22:35:49 2020

@author: jijo
"""
import pymongo as pm

class config:
    def opendb():
        myclient = pm.MongoClient("mongodb://localhost:27017/")
        mydb = myclient["idp"]
        return(mydb)
        
        