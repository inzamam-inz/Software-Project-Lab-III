from flask import Flask, request, redirect
from flask_restful import Resource, Api
from flask_cors import CORS
import os
import prediction as rs
import json
import numpy as np
import additional as ad
from flask import jsonify


app = Flask(__name__)
cors = CORS(app, resources={r"*": {"origins": "*"}})
api = Api(app)


# Custom JSON encoder for handling NumPy types
class NumpyEncoder(json.JSONEncoder):
    def default(self, obj):
        if isinstance(obj, np.integer):
            return int(obj)
        elif isinstance(obj, np.floating):
            return float(obj)
        elif isinstance(obj, np.ndarray):
            return obj.tolist()
        return super(NumpyEncoder, self).default(obj)


class Server(Resource):
    def get(self):
        return 'Yes, server is running!'

    def post(self):
        try:
            value = request.get_json()
            if value:
                return {'Post Values': value}, 201

            return {"error": "Invalid format."}

        except Exception as error:
            return {'error': error}


class GetPredictionOutput(Resource):
    def get(self, id):
        return json.dumps(rs.movie_predict(int(id)), cls=NumpyEncoder)

    def post(self):
        try:
            # data = request.get_json()
            # predict = prediction.predict_mpg(data)
            # predict_output = predict
            return {'predict': "predict_output"}

        except Exception as error:
            return {'error': error}


class Model(Resource):
    def get(self):
        rs.mb.save_model()
        return {"message": "Model rebuilt and saved successfully."}

    def post(self):
        return {"error": "Invalid Method."}


class addUserFeedback(Resource):
    def get(self):
        return {"error": "Invalid Method."}

    def post(self):
        try:
            value = request.get_json()
            print(value)
            if value:
                res = ad.add_data(value)
                return {'Post Values': res}, 201, {'Content-Type': 'application/json'}
            return {"error": "Invalid format."}

        except Exception as error:
            # Handle the exception and return a JSON response
            error_message = str(error)
            response = {'error': error_message}
            return jsonify(response), 500


class add_project(Resource):
    def get(self):
        return {"error": "Invalid Method."}

    def post(self):
        try:
            value = request.get_json()
            print(value)
            if value:
                ad.add_project(value)
                print("Model building started...")
                ad.build_model()
                print("Model building completed.")
                return {'Post Values': "res"}, 201, {'Content-Type': 'application/json'}
            return {"error": "Invalid format."}

        except Exception as error:
            # Handle the exception and return a JSON response
            error_message = str(error)
            response = {'error': error_message}
            return jsonify(response), 500


api.add_resource(Model, '/model')
api.add_resource(Server, '/')
api.add_resource(GetPredictionOutput, '/getPredictionOutput/<string:id>')
api.add_resource(addUserFeedback, '/adduserfeedback')
api.add_resource(add_project, '/addproject')


if __name__ == "__main__":
    print("Server is running...")
    port = int(os.environ.get("PORT", 9000))
    app.run(host='0.0.0.0', port=port)
