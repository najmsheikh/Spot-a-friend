from flask import Flask, render_template
from flask_socketio import SocketIO, emit

from fbrecog import recognize
from PIL import Image

import uuid
import json
import io

app = Flask(__name__)
socketio = SocketIO(app)

response_data = []


def recognize_image(imagepath):
    path = imagepath						# Direct link to the image being recognized
    access_token = '***REMOVED***'			# Access token from the Graph API explorer
    cookie = '***REMOVED***'				# Facebook session cookie for the target account
    fb_dtsg = '***REMOVED***'				# Form data parameter -- extracted from sample request

    response_data = recognize(path, access_token, cookie, fb_dtsg)

    if not response_data:
        socketio.emit('new_message', {
                      'new_message': 'Sorry, I could not recognize this person.'})
    else:
        # Internal logging
        print(response_data)

        # If one person is recognized
        if len(response_data) == 1:
            location = ''
            if response_data[0]['x'] <= 40:
                location = 'left'
            elif response_data[0]['x'] > 40 and response_data[0]['x'] <= 60:
                location = 'center'
            else:
                location = 'right'
            name = response_data[0]['name']
            speech_response = 'You have ' + name + ' on your ' + location
            socketio.emit('new_message', {'found': speech_response})

        # If multiple people are recognized
        # TODO: Fix the hacky assigment
        else:
            arr = []
            for i in range(0, len(response_data)):
                location = ''
                if response_data[i]['x'] <= 40:
                    location = 'left'
                elif response_data[i]['x'] > 40 and response_data[i]['x'] <= 60:
                    location = 'center'
                else:
                    location = 'right'
                arr.append({'name': response_data[i][
                           'name'], 'location': location})
            speech_response = 'You have ' + arr[0]['name'] + ' on your ' + arr[0][
                'location'] + ' and ' + arr[1]['name'] + ' on your ' + arr[1]['location']
            socketio.emit('new_message', {'found': speech_response})


@app.route("/")
def index():
    return render_template('index.html')


@socketio.on('send_message')
def handle_source(json_data):
    print(json_data)
    # text = json_data['message'].encode('ascii', 'ignore')
    # socketio.emit('echo', {'echo': 'test.jpg'})


@socketio.on('send_image')
def image_sent(raw):
    fileName = str(uuid.uuid4())
    enrollmentMessage = 'New image enrolled: ' + fileName
    print(enrollmentMessage)
    img = Image.open(io.BytesIO(raw))
    path = fileName + '.jpg'
    img.save(path)
    # print(path)
    recognize_image(path)


if __name__ == "__main__":
    socketio.run(app)
