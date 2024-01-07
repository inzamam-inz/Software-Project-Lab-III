import pickle
from tensorflow.keras.models import load_model
import numpy as np
import model_builder as mb


def movie_predict(user_id, count=30, num_movies=9725):
    try:
        with open("model.pkl", 'rb') as file:
            pickle_model = pickle.load(file)

    except Exception as error:
        mb.save_model()
        with open("model.pkl", 'rb') as file:
            pickle_model = pickle.load(file)

    users = []
    movies = []
    for i in range(num_movies):
        users.append(user_id)
        movies.append(i)

    test_user_id = np.array(users)
    test_movie_id = np.array(movies)
    prediction = np.round(pickle_model.predict([test_user_id, test_movie_id]), decimals=2)

    rating = []
    for i in prediction:
        rating.append(i[0])
    rating = np.array(rating)

    # return top 10 rated movies with movie id
    top_rated = rating.argsort()[-count:][::-1]
    output = []
    for i in top_rated:
        output.append([i, rating[i]])

    print(output)
    return output

# movie_predict(1)