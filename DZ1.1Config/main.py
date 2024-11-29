import os
import zipfile
import json
import datetime
import sys
import argparse

class ShellEmulator:
    # (Остальной код остается без изменений)

    def execute_command(self, command):
        """Обрабатывает и выполняет команды."""
        parts = command.strip().split()
        if not parts:
            return

        cmd = parts[0]

        if cmd == "ls":
            self.ls()
        elif cmd == "cd":
            if len(parts) > 1:
                self.cd(parts[1])
            else:
                print("cd: ожидается аргумент")
        elif cmd == "who":
            self.who()
        elif cmd == "whoami":
            self.whoami()
        elif cmd == "find":
            if len(parts) > 1:
                self.find(parts[1])
            else:
                self.find()  # Поиск без аргументов
        elif cmd == "date":
            self.show_date()
        elif cmd == "exit":
            print("Exiting shell.")
            sys.exit(0)
        else:
            print(f"Команда не найдена: {cmd}")

        # Логируем каждую команду
        self.log_action(command)

    def find(self, name=None):
        """Ищет файлы и директории в текущей директории."""
        abs_path = os.path.join(self.virtual_fs_root, self.current_directory.lstrip("/"))
        if not os.path.exists(abs_path):
            print("Directory not found")
            return

        results = []
        for root, dirs, files in os.walk(abs_path):
            # Относительный путь для текущего каталога
            rel_root = os.path.relpath(root, self.virtual_fs_root)
            if name:
                # Ищем совпадения по имени
                results.extend([os.path.join(rel_root, d) for d in dirs if name in d])
                results.extend([os.path.join(rel_root, f) for f in files if name in f])
            else:
                # Если имя не задано, просто добавляем все
                results.extend([os.path.join(rel_root, d) for d in dirs])
                results.extend([os.path.join(rel_root, f) for f in files])

        if results:
            print("\n".join(results))
        else:
            print(f"Ничего не найдено{' с именем ' + name if name else ''}.")

    def show_date(self):
        """Выводит текущую дату и время."""
        now = datetime.datetime.now()
        print(now.strftime("%Y-%m-%d %H:%M:%S"))
