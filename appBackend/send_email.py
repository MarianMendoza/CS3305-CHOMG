from email.mime.text import MIMEText
import os
import smtplib
import ssl
import time
import sys
from watchdog.observers import Observer
from watchdog.events import FileSystemEventHandler
from email.message import EmailMessage
from email.mime.multipart import MIMEMultipart
from email.mime.base import MIMEBase
from email import encoders
from datetime import datetime
import requests


class Watcher:
    DIRECTORY_TO_WATCH = "CHOMGS/rec"

    def __init__(self):
        self.observer = Observer()

    def run(self):
        event_handler = Handler()
        self.observer.schedule(event_handler, self.DIRECTORY_TO_WATCH, recursive=True)
        self.observer.start()
        try:
            while True:
                time.sleep(5)
        except:
            self.observer.stop()
            print("Observer Stopped")

        self.observer.join()


class Handler(FileSystemEventHandler):

    @staticmethod
    def on_created(event):
        if event.is_directory:
            return None

        elif event.src_path.endswith('.mp4'):
            print(f"Received recording - {event.src_path}. Sending email...")
            send_email(event.src_path)

def get_first_user_email():
    try:
        response = requests.get('http://localhost:3000/getFirstUserEmail')
        if response.status_code == 200:
            return response.json()['email']
        else:
            print(f"Error: Unable to fetch email. Status code: {response.status_code}")
            return None
    except requests.exceptions.RequestException as e:
        print(f"Request failed: {e}")
        return None
    
def send_email(video_path):
    emailSender = "chomgscs3305@gmail.com"
    # emailPassword = os.environ.get("emailPassword")
    #actual password in snap gc
    emailPassword = "trtv hcqj fwhp ptwf"
    
    emailReceiver = get_first_user_email()  # Get the email from your Node.js server
    if emailReceiver is None:
        print("No email receiver found. Email not sent.")
        return

    timestamp = datetime.now().strftime("%Y-%m-%d %H:%M:%S")

    subject = "Security Anomaly Detected!"
    body = f"""<h1>Security Alert</h1>
        <p>A security anomaly was recorded at {timestamp}. Please find the attached video for details.</p>
        """

    em = MIMEMultipart()
    em['From'] = emailSender
    em['To'] = emailReceiver
    em['Subject'] = subject
    em.attach(MIMEText(body, 'html'))

    with open(video_path, 'rb') as file:
        video = MIMEBase('application', 'octet-stream')
        video.set_payload(file.read())

    encoders.encode_base64(video)
    video.add_header('Content-Disposition', f'attachment; filename={os.path.basename(video_path)}',)
    em.attach(video)

    context = ssl.create_default_context()

    with smtplib.SMTP_SSL('smtp.gmail.com', 465, context=context) as smtp:
        smtp.login(emailSender, emailPassword)
        smtp.send_message(em)
        print("Email sent!")

if __name__ == "__main__":
    if len(sys.argv) == 3:
        recipient_email = sys.argv[1]
        reset_link = sys.argv[2]
        send_email(recipient_email, reset_link)
    else:
        print("Invalid arguments")