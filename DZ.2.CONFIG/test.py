import os
import tarfile
import shutil
import csv
import tkinter as tk
from tkinter import filedialog, messagebox
from datetime import datetime
import configparser
import getpass


class ShellEmulator:
    def __init__(self, config_file='config.ini'):
        self.load_config(config_file)
        self.virtual_fs = './vfs_temp'  # Временная директория для распаковки файлов
        self.current_dir = os.path.abspath(self.virtual_fs)
        self.log_file = self.config['Settings']['log_file']
        self.setup_vfs()

    def load_config(self, config_file):
        self.config = configparser.ConfigParser()
        self.config.read(config_file)

    def setup_vfs(self):
        vfs_archive = self.config['Settings']['vfs_archive']
        if os.path.exists(self.virtual_fs):
            shutil.rmtree(self.virtual_fs)  # Очистка старой файловой системы
        os.mkdir(self.virtual_fs)
        with tarfile.open(vfs_archive, 'r') as archive:
            archive.extractall(self.virtual_fs)  # Извлечение в виртуальную файловую систему

    def log_action(self, command, result='Success'):
        with open(self.log_file, 'a', newline='') as file:
            writer = csv.writer(file)
            writer.writerow([datetime.now().strftime('%Y-%m-%d %H:%M:%S'), command, result])

    def ls(self):
        try:
            files = os.listdir(self.current_dir)
            self.log_action('ls')
            return '\n'.join(files)
        except FileNotFoundError as e:
            self.log_action('ls', 'Error')
            return f'Error: {str(e)}'

    def cd(self, path):
        try:
            new_path = os.path.join(self.current_dir, path)
            if os.path.isdir(new_path):
                self.current_dir = new_path
                self.log_action(f'cd {path}')
            else:
                return FileNotFoundError(f"Directory '{path} not found")
        except Exception as e:
            self.log_action(f'cd {path}', 'Error')

    def find(self, filename):
        try:
            found_files = []
            for root, dirs, files in os.walk(self.virtual_fs):
                if filename in files:
                    full_path = os.path.join(root, filename)
                    if full_path.startswith(self.current_dir):
                        found_files.append(os.path.relpath(full_path, self.current_dir))
            self.log_action(f'find {filename}')
            return '\n'.join(found_files) if found_files else "No matches found."
        except Exception as e:
            self.log_action(f'find {filename}', 'Error')
            return str(e)

    def whoami(self):
        try:
            user = getpass.getuser()
            self.log_action('whoami')
            return user
        except Exception as e:
            self.log_action('whoami', 'Error')
            return str(e)

    def date(self):
        current_date = datetime.now().strftime('%Y-%m-%d %H:%M:%S')
        self.log_action('date')
        return current_date

    def exit(self):
        self.log_action('exit')
        shutil.rmtree(self.virtual_fs, ignore_errors=True)  # Удаление временной файловой системы
        exit(0)


# GUI часть программы
class ShellGUI:
    def __init__(self, root, emulator):
        self.root = root
        self.emulator = emulator
        self.root.title("Shell Emulator")
        self.output = tk.Text(root, height=20, width=80, state='disabled')
        self.output.pack()

        self.command_entry = tk.Entry(root, width=80)
        self.command_entry.pack()
        self.command_entry.bind('<Return>', self.run_command)

    def run_command(self, event):
        command = self.command_entry.get().strip()
        result = self.process_command(command)
        self.display_output(command, result)

    def process_command(self, command):
        if command.startswith('ls'):
            return self.emulator.ls()
        elif command.startswith('cd'):
            path = self.safe_split(command, 1)
            return self.emulator.cd(path) or ''
        elif command.startswith('find'):
            filename = self.safe_split(command, 1)
            return self.emulator.find(filename)
        elif command == 'whoami':
            return self.emulator.whoami()
        elif command == 'date':
            return self.emulator.date()
        elif command == 'exit':
            self.emulator.exit()
        else:
            return "Unknown command."

    def safe_split(self, command, index):
        try:
            return command.split()[index]
        except IndexError:
            return ''

    def display_output(self, command, result):
        self.output.config(state='normal')
        self.output.insert(tk.END, f"$ {command}\n{result}\n")
        self.output.config(state='disabled')
        self.command_entry.delete(0, tk.END)


if __name__ == '__main__':
    emulator = ShellEmulator()
    root = tk.Tk()
    gui = ShellGUI(root, emulator)
    root.mainloop()

### Тесты (`tests.py`):
import unittest
from emulator import ShellEmulator


class TestShellEmulator(unittest.TestCase):
    def setUp(self):
        self.emulator = ShellEmulator()

    def tearDown(self):
        self.emulator.exit()  # Удаляем виртуальную файловую систему после тестов

    def test_ls(self):
        self.assertIn('file1.txt', self.emulator.ls())

    def test_cd(self):
        self.emulator.cd('folder')
        self.assertEqual(self.emulator.current_dir, './vfs_temp/folder')

    def test_find(self):
        self.assertIn('./vfs_temp/file1.txt', self.emulator.find('file1.txt'))

    def test_whoami(self):
        self.assertIsNotNone(self.emulator.whoami())

    def test_date(self):
        self.assertIsNotNone(self.emulator.date())

    def test_exit(self):
        self.emulator.exit()
        self.assertFalse(os.path.exists(self.emulator.virtual_fs))


if __name__ == '__main__':
    unittest.main()