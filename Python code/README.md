# cyclepaths_project

This folder contains the Python code for the analysis of the trips.

1-data_collection: it contains the script for generating synthetic data

2-preprocessing: it contains the scripts used for the one-time preprocessing of the trip data and the cycle paths data

3-data_analysis: it contains the two classes which implements the two algorithms used in the analysis of the trips

4-database_handling: it contains all the classes that are used to do the operations related to the MongoDB database

main: it contains the controller, which is the script to be launched to execute the analysis on one trip;
	controller_to_analyse_all_trips, which is the script to be launched to do the analysis on all the trips in the database
	export_to_csv, a class used to export the result of the analysis in csv format
	config, a configuration file to login to the database