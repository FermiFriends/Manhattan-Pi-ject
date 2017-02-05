import threading
import flask
import bomb
import json
import serial
from flask import Flask, request


app = Flask(__name__)
bomb_session = None


@app.route('/', methods=['POST', 'GET'])
def arm_bomb():
    global bomb_session

    if bomb_session is not None:
        bomb.kill_bomb_session(bomb_session)

    if request.method == 'POST':
        options = request.get_json()
    else:
        options = None

    bomb_session = bomb.start_bomb_session(options)

    return "", 200


@app.route('/status')
def get_status():
    global bomb_session
    return json.dumps(bomb.session_status(bomb_session)), 200


@app.route('/test')
def test():
    return 'test endpoint pls ignore'


if __name__ == '__main__':

    app.run(debug=True, host='0.0.0.0')
