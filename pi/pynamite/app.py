import flask
from flask import Flask, request
import json


app = Flask(__name__)


@app.route('/', methods=['POST'])
def arm_bomb():

    if flask.g.bomb_session is not None:
        kill_bomb_session(flask.g.bomb_session)

    options = request.get_json()

    flask.g.bomb_session = start_bomb_session(options)

    return "", 200


@app.route('/status')
def get_status():
    return json.dumps(session_status(flask.g.bomb_session)), 200



@app.route('/test')
def test():
    return 'test endpoint pls ignore'


if __name__ == '__main__':
    app.run(debug=True, host='0.0.0.0')
