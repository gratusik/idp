import pymongo as pm
import numpy as np
import json

eps = 1e-3
min_points = 10

with open('raw_trips_input.json', 'r') as f:
    trips_list = json.load(f)
    f.close()

new_trips = []
for trip in trips_list:
    trip["properties"].pop("stret_name", None)
    trip["properties"]["type"]="trip"

for trip in trips_list:
    l=len(trip["tracepoints"]["latitudes"])
    dlat = np.abs(trip["tracepoints"]["latitudes"][0]-trip["tracepoints"]["latitudes"][l-1])
    dlon = np.abs(trip["tracepoints"]["longitudes"][0]-trip["tracepoints"]["longitudes"][l-1])
    if l>=min_points and (dlat>eps or dlon>eps):
        doc = {"_id":trip["_id"],"type": "trip", "properties": {"detectedMode":"Bicycle","date":trip["properties"]["date"],"startTime":trip["properties"]["startTime"],"endTime":trip["properties"]["endTime"]},"tracepoints": {"latitudes": trip["tracepoints"]["latitudes"], "longitudes": trip["tracepoints"]["longitudes"]}}
        new_trips.append(doc)

print("NT =",len(new_trips))
with open('../data/app_trips/app_trips.json', 'w') as outfile:
    json.dump(new_trips, outfile)
    outfile.close()

print("Done.")
