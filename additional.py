import csv


import pandas as pd
import numpy as np
import pickle
from sklearn.metrics import mean_absolute_error
from sklearn.model_selection import train_test_split

import tensorflow as tf
from tensorflow.keras.layers import Embedding, Flatten, Input, Dropout, Dense, Concatenate, Dot
from tensorflow.keras.models import Model
from tensorflow.keras.optimizers import Adam
from tensorflow.keras.utils import plot_model

import matplotlib.pyplot as plt
from IPython.display import SVG


# Define the model
def MLNMFPre():
    # Load the dataset
    dataset = pd.read_csv('Dataset/projects.csv', header=0,
                          names=['user_id', 'movie_id', 'rating', 'timestamp'])

    print(dataset)

    # Need to map movie ID to [1, num_movies]
    movie_id_to_new_id = dict()
    id = 1
    for index, row in dataset.iterrows():
        if movie_id_to_new_id.get(row['movie_id']) is None:
            movie_id_to_new_id[row['movie_id']] = id
            dataset.at[index, 'movie_id'] = id
            id += 1
        else:
            dataset.at[index, 'movie_id'] = movie_id_to_new_id.get(row['movie_id'])

    # Define the new row as a dictionary where keys are column names and values are the data for each column

    # Append the new row to the DataFrame
    # dataset = pd.concat([dataset, pd.DataFrame.from_records([new_row])])

    num_users = len(dataset.user_id.unique())
    num_movies = len(dataset.movie_id.unique())
    print(num_users, num_movies)
    # train, test = train_test_split(dataset, test_size=0.2)
    train = dataset

    latent_dim = 10

    # Define inputs
    movie_input = Input(shape=[1], name='movie-input')
    user_input = Input(shape=[1], name='user-input')

    # MLP Embeddings
    movie_embedding_mlp = Embedding(num_movies + 1, latent_dim, name='movie-embedding-mlp')(movie_input)
    movie_vec_mlp = Flatten(name='flatten-movie-mlp')(movie_embedding_mlp)

    user_embedding_mlp = Embedding(num_users + 1, latent_dim, name='user-embedding-mlp')(user_input)
    user_vec_mlp = Flatten(name='flatten-user-mlp')(user_embedding_mlp)

    # MF Embeddings
    movie_embedding_mf = Embedding(num_movies + 1, latent_dim, name='movie-embedding-mf')(movie_input)
    movie_vec_mf = Flatten(name='flatten-movie-mf')(movie_embedding_mf)

    user_embedding_mf = Embedding(num_users + 1, latent_dim, name='user-embedding-mf')(user_input)
    user_vec_mf = Flatten(name='flatten-user-mf')(user_embedding_mf)

    # MLP layers
    concat_mlp = Concatenate(name='concat-mlp')([movie_vec_mlp, user_vec_mlp])
    fc_1_mlp = Dense(2000, name='fc-1-mlp', activation='relu')(concat_mlp)
    fc_2_mlp = Dense(500, name='fc-2-mlp', activation='relu')(fc_1_mlp)
    fc_3_mlp = Dense(20, name='fc-3-mlp', activation='relu')(fc_2_mlp)

    # Prediction from MLP
    pred_mlp = Dense(10, name='pred-mlp', activation='relu')(fc_3_mlp)

    # Prediction from MF
    pred_mf = Dot(axes=1, name='pred-mf')([movie_vec_mf, user_vec_mf])

    # Combine predictions from both MLP and MF
    combine_mlp_mf = Concatenate(name='combine-mlp-mf')([pred_mlp, pred_mf])

    # Final result
    result = Dense(1, name='result', activation='relu')(combine_mlp_mf)

    model = Model([user_input, movie_input], result)
    model.compile(optimizer=Adam(lr=0.01), loss='mean_absolute_error')

    # Use plot_model for model visualization
    plot_model(model, show_shapes=True, to_file='model.png', dpi=64)

    # Model Summary
    model.summary()

    # Train the model and store the training history

    history = model.fit([train.user_id, train.movie_id], train.rating, epochs=3)

    # Plot the training loss
    # plt.plot(history.history['loss'], marker='o', linestyle='-')
    # plt.yscale('log')
    # plt.xlabel("Epoch")
    # plt.ylabel("Train Error")
    # plt.show()

    # Predict ratings on the test set
    # y_hat = np.round(model.predict([test.user_id, test.movie_id]), decimals=2)
    # y_true = test.rating
    #
    # # Calculate Mean Absolute Error
    #
    # mae = mean_absolute_error(y_true, y_hat)
    # print("Mean Absolute Error:", mae)

    return model


# Train the model
def MLNMF(model):
    return model


# Save the model
def save_model():
    print("Saving model...")
    model = MLNMFPre()
    print("Model saved successfully.")
    # model = MLNMF(model)

    # Save to file in the current working directory
    pkl_filename = "project.pkl"
    with open(pkl_filename, 'wb') as file:
        pickle.dump(model, file)

    print("Model saved successfully.")


def add_data(value):
    print(value)
    # Specify the CSV file path
    csv_file_path = './Dataset/ratings.csv'

    # Open the CSV file in append mode
    with open(csv_file_path, 'a', newline='') as file:
        # Create a CSV writer object
        csv_writer = csv.writer(file)

        # Write the header if the CSV file is empty
        if file.tell() == 0:
            header = value[0].keys()
            csv_writer.writerow(header)

        # Write the new data to the CSV file
        for row in value:
            csv_writer.writerow(row.values())

    print("Data added successfully.")


def add_project(value):
    print(value)
    # Specify the CSV file path
    csv_file_path = './Dataset/projects.csv'
    open(csv_file_path, 'w').close()

    # Open the CSV file in append mode
    with open(csv_file_path, 'a', newline='') as file:
        # Create a CSV writer object
        csv_writer = csv.writer(file)

        # Write the header if the CSV file is empty
        if file.tell() == 0:
            header = value[0].keys()
            csv_writer.writerow(header)

        # Write the new data to the CSV file
        for row in value:
            csv_writer.writerow(row.values())

    print("Data added successfully.")


def build_model():
    save_model()