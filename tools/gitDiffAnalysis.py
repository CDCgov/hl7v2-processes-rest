import subprocess
import os

def get_changed_and_new_directories():
    # Get a list of changed files compared to the last commit
    changed_files = subprocess.run(['git', 'diff', '--name-only', 'HEAD'], stdout=subprocess.PIPE, text=True)
    changed_files_list = changed_files.stdout.strip().split('\n')

    # Get a list of new/untracked files
    new_files = subprocess.run(['git', 'ls-files', '--others', '--exclude-standard'], stdout=subprocess.PIPE, text=True)
    new_files_list = new_files.stdout.strip().split('\n')

    # Combine both lists and remove empty strings
    all_files = filter(None, changed_files_list + new_files_list)

    # Extract directories from file paths
    directories = {os.path.dirname(file) for file in all_files if file}

    # Return the directories in a list format, filtering out empty strings (root level)
    return list(filter(None, directories))

if __name__ == "__main__":
    directories = get_changed_and_new_directories()
    print(directories)
