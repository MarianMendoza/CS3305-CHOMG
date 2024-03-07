from email.mime.text import MIMEText
import os
import smtplib
import ssl
from watchdog.observers import Observer
from watchdog.events import FileSystemEventHandler
from email.mime.multipart import MIMEMultipart
from datetime import datetime
import threading
import time


# hardcoded to account for spam filtering and errors in sample acount email addresses. See bottom of code to see how this would be implemented
#using Mongo db, which would email all users
class Watcher:
    def __init__(self, directory_to_watch, email_receiver):
        self.observer = Observer()
        self.directory_to_watch = directory_to_watch
        self.email_receiver = email_receiver
        self.lock = threading.Lock()

    def run(self):
        event_handler = Handler(self)
        self.observer.schedule(event_handler, self.directory_to_watch, recursive=True)
        self.observer.start()
        self.schedule_periodic_summary()  # Start the periodic summary immediately
        try:
            while True:
                # Keep the thread alive to avoid interpreter shutdown issues.
                time.sleep(1)
        finally:
            self.observer.stop()
            self.observer.join()

    def schedule_periodic_summary(self):
        # Schedule the email to be sent every 86400 seconds (day)
        threading.Timer(86400, self.send_periodic_summary).start()

    def send_periodic_summary(self):
        # Count the number of files in the directory
        file_count = len([name for name in os.listdir(self.directory_to_watch) if os.path.isfile(os.path.join(self.directory_to_watch, name))])
        send_email_summary(self.email_receiver, file_count)
        self.schedule_periodic_summary()  # Schedule the next summary

class Handler(FileSystemEventHandler):
    def __init__(self, watcher):
        self.watcher = watcher

    def on_created(self, event):
        # This method is kept for possible future use or logging
        if not event.is_directory:
            print(f"New file detected - {event.src_path}.")

def send_email_summary(email_receiver, count):
    emailSender = "chomgscs3305@gmail.com"
    emailPassword = "trtv hcqj fwhp ptwf"

    subject = "Daily Video Summary"
    body = f"""<p>Today's video summary: </p>
<p>A total of {count} videos were stored today in your directory. </p>
"""

    em = MIMEMultipart()
    em['From'] = emailSender
    em['To'] = email_receiver
    em['Subject'] = subject
    em.attach(MIMEText(body, 'html'))

    context = ssl.create_default_context()

    with smtplib.SMTP_SSL('smtp.gmail.com', 465, context=context) as smtp:
        smtp.login(emailSender, emailPassword)
        smtp.send_message(em)
        print(f"Periodic summary email sent to {email_receiver}!")

def start_watchers():
    email = 'chomgscs3305@gmail.com'
    directory_to_watch = "/root/CHOMG/recordedFootage/chomgscs3305@gmail.com"
    watcher = Watcher(directory_to_watch, email)
    watcher_thread = threading.Thread(target=watcher.run)
    watcher_thread.start()
    return watcher_thread

if __name__ == "__main__":
    watcher_thread = start_watchers()
    watcher_thread.join()  # This will keep the main thread running while the watcher_thread does its job

# How this would be implemented with a Mongodb connection
'''

MONGO_USER = os.getenv('MONGO_USER')
MONGO_PASSWORD = os.getenv('MONGO_PASSWORD')
MONGO_HOST = os.getenv('MONGO_HOST')
MONGO_DB = os.getenv('MONGO_DB')

# Construct the MongoDB URI
mongoURI = f"mongodb://{MONGO_USER}:{MONGO_PASSWORD}@{MONGO_HOST}:27017/{MONGO_DB}?authSource=admin"
client = MongoClient(mongoURI)
db = client['AppUsers']  # Database name
users_collection = db['users']  # Collection name

# code stays the same, except for def start_watchers() which will look like this

def start_watchers():
    email_addresses = users_collection.find({}, {'username': 1})
    for user in email_addresses:
        if 'username' in user:
            email = user['username']
            print(email)
            directory_to_watch = f"/absolute/path/to/CHOMG/recordedFootage/{email}"

            watcher = Watcher(directory_to_watch, email)
            watcher_thread = threading.Thread(target=watcher.run)
            watcher_thread.start()
            return watcher_thread
  '''
