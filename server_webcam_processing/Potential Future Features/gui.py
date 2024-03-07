# ChatGTP
import tkinter as tk
from tkinter import messagebox
from tkinter import ttk
import threading
import time

class CHOMGApp:
    def __init__(self, root):
        self.root = root
        self.root.title("CHOMG GUI")
        self.root.geometry("400x300")
        self.root.configure(bg="red")

        self.phase = 1
        self.room_code_var = tk.StringVar()
        self.countdown_var = tk.StringVar()

        self.create_widgets()

    def create_widgets(self):
        # CHOMG Label
        chomg_label = tk.Label(self.root, text="CHOMG", font=("Sans Serif Collection", 24, "bold"), bg="red", fg="white")
        chomg_label.pack(side="top", pady=10)

        # Phase 1: Room Code Entry
        if self.phase == 1:
            room_code_label = tk.Label(self.root, text="Enter Room Code:", font=("Sans Serif Collection", 12, "bold"), bg="red", fg="white")
            room_code_label.pack(pady=10)

            room_code_entry = tk.Entry(self.root, textvariable=self.room_code_var)
            room_code_entry.pack(pady=10)

            next_button = tk.Button(self.root, text="Next", command=self.phase2, bg="white", fg="red")
            next_button.pack(pady=10)

    def phase2(self):
        self.phase = 2
        self.clear_widgets()

        # CHOMG Label
        chomg_label = tk.Label(self.root, text="CHOMG", font=("Helvetica", 24, "bold"), bg="red", fg="white")
        chomg_label.pack(side="top", pady=10)

        # Phase 2: Save Results Checkbox and Run Button
        save_results_checkbox = tk.Checkbutton(self.root, text="Save Results", bg="red", fg="white")
        save_results_checkbox.pack(pady=10)

        run_button = tk.Button(self.root, text="Run", command=self.phase3, bg="white", fg="red")
        run_button.pack(pady=10)

    def phase3(self):
        self.phase = 3
        self.clear_widgets()

        # CHOMG Label
        chomg_label = tk.Label(self.root, text="CHOMG", font=("Helvetica", 24, "bold"), bg="red", fg="white")
        chomg_label.pack(side="top", pady=10)

        # Phase 3: Countdown
        countdown_label = tk.Label(self.root, textvariable=self.countdown_var, font=("Helvetica", 16), bg="red", fg="white")
        countdown_label.pack(pady=20)

        # Start Countdown Thread
        countdown_thread = threading.Thread(target=self.run_countdown)
        countdown_thread.start()

    def run_countdown(self):
        for i in range(30, 0, -1):
            self.countdown_var.set(f"{i}s until CHOMG activation.\n Please leave the room.")
            time.sleep(1)

        self.countdown_var.set("Running...")

    def clear_widgets(self):
        for widget in self.root.winfo_children():
            widget.destroy()

if __name__ == "__main__":
    root = tk.Tk()
    app = CHOMGApp(root)
    root.mainloop()
