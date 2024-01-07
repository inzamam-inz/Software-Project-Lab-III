import csv
import json
import requests

api_url = "http://localhost:8888/api/movies"

# Read CSV file and make POST requests
with open('merged_data.csv', 'r') as csv_file:
    reader = csv.DictReader(csv_file)

    for row in reader:
        # Modify the data dictionary to include all your columns
        data = {
            "movieId": row["movieId"],
            'title': row['title'],
            'genres': row['genres'],
            'imdbId': row['imdbId'],
            'tmdbId': row['tmdbId'],
            'year': row['year'],
        }

        # Print the data dictionary
        print(data["movieId"])

        # Set the Content-Type header to application/json
        headers = {'Content-Type': 'application/json'}

        # Convert data to JSON format
        json_data = json.dumps(data)

        # Make the POST request with json parameter
        response = requests.post(api_url, data=json_data, headers=headers)

        # Print the status code
        # print(response.status_code)
        #
        # break





# ----------------------------------------------------------------- #
# ------------------------ Data Preprocessing --------------------- #
# import pandas as pd
# import numpy as np
#
# # Read the CSV files into pandas dataframes
# movies_df = pd.read_csv('./Dataset/movies.csv')
# links_df = pd.read_csv('./Dataset/links.csv')
#
# print(movies_df)
# print(links_df)
#
# # Merge the dataframes on the 'id' column
# merged_df = pd.merge(movies_df, links_df, on='movieId')
# merged_df['tmdbId'] = merged_df['tmdbId'].replace([np.inf, -np.inf], np.nan).fillna(0).astype(int)
#
# # Extract the release year from the 'title' column and create a new 'year' column
# merged_df['year'] = merged_df['title'].str.extract('\((\d{4})\)', expand=False)
#
# # Convert the 'year' column to integers
# merged_df['year'] = merged_df['year'].astype(float).astype(pd.Int64Dtype())
#
# # Remove the year part from the 'title' column
# merged_df['title'] = merged_df['title'].str.replace(r' \(\d{4}\)', '', regex=True)
#
# # Print or save the merged dataframe
# print(merged_df)
#
# # If you want to save the merged dataframe to a new CSV file
# merged_df.to_csv('merged_data.csv', index=False)
