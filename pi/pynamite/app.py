from flask import Flask, request


app = Flask(__name__)

@app.route('/test')
def test():
    # TODO
    return 'test page pls ignore'

@app.route('/', methods=['POST'])
def arm_bomb():
    # TODO
    return 'Hello, World!'

@app.route('/status')
def get_status():
    # TODO
    return 'Hello, World!'


if __name__ == '__main__':
    app.run(debug=True, host='0.0.0.0')
