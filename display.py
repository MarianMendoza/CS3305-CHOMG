# https://python-win32.python.narkive.com/3e75rNuo/turning-monitors-off-from-python
import  win32con
import win32gui
def turnOffWindowsDisplay():
    SC_MONITORPOWER = 0xF170
    win32gui.SendMessage(win32con.HWND_BROADCAST, win32con.WM_SYSCOMMAND, SC_MONITORPOWER, 2)