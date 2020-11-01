import sys; sys.path.append("../data/cycle_paths")
import pymongo as pm
import json

## NEW VERSION
with open('existing_cyclepaths_exported_from_QGIS.json', 'r') as f:
    paths_list = json.load(f)
    f.close()

for path in paths_list:
    path["type"]="CyclePath"
    path["properties"]["ID"]=path["properties"]["NUMERO PRO"]
    path["properties"].pop("NUMERO PRO", None)
    path["properties"]["street_name"]=path["properties"]["Nome_MP12"]
    path["properties"].pop("Nome_MP12", None)
    path["properties"]["length"]=path["properties"]["Lungh_MP11"]
    path["properties"].pop("Lungh_MP11",None)
    path["properties"]["city"]="TORINO"
    path["existing"]=True
    path["tag"]="old"
    path["geometry"]["latitudes"]=[]
    path["geometry"]["longitudes"]=[]
    for point in path["geometry"]["coordinates"][0]:
        path["geometry"]["longitudes"].append(point[0])
        path["geometry"]["latitudes"].append(point[1])
    path["geometry"].pop("coordinates", None)

names_set = []
[names_set.append(path["properties"]["street_name"]) for path in paths_list if path["properties"]["street_name"] not in names_set]
new_paths = []

i=1
for name in names_set:
    doc = {"type": "path", "properties": {"ID": i, "city":"TORINO", "street_name":name, "length": 0.0},"existing": True, "tag":"old","geometry": {"type": "MultiLineString", "latitudes": [], "longitudes": []}}

    for path in paths_list:
        if path["properties"]["street_name"] == name:
            doc["geometry"]["latitudes"].append(path["geometry"]["latitudes"])
            doc["geometry"]["longitudes"].append(path["geometry"]["longitudes"])
            if path["properties"]["length"]:
                doc["properties"]["length"]+= path["properties"]["length"]

    new_paths.append(doc)
    i+=1

with open('../data/cycle_paths/existing_cyclepaths.json', 'w') as outfile:
    json.dump(new_paths, outfile)
    outfile.close()

print("Done.")
