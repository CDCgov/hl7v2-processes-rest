import subprocess
import os
import fnmatch
import argparse

def get_changed_and_new_directories(exclude_patterns=None):
    if exclude_patterns is None:
        exclude_patterns = []

    # Helper function to check if a path matches any exclusion pattern
    def is_excluded(path):
        # Check both the file path and the directory it is in
        if any(fnmatch.fnmatch(path, pattern) for pattern in exclude_patterns):
            return True
        # Check if the directory itself is excluded
        directory = os.path.dirname(path)
        return any(fnmatch.fnmatch(directory, pattern) for pattern in exclude_patterns)
    
    # Get a list of changed files compared to the last commit
    changed_files = subprocess.run(['git', 'diff', '--name-only', 'HEAD'], stdout=subprocess.PIPE, text=True)
    changed_files_list = changed_files.stdout.strip().split('\n')

    # Get a list of new/untracked files
    new_files = subprocess.run(['git', 'ls-files', '--others', '--exclude-standard'], stdout=subprocess.PIPE, text=True)
    new_files_list = new_files.stdout.strip().split('\n')

    # Combine both lists and remove empty strings
    all_files = filter(None, changed_files_list + new_files_list)

    # Extract directories from file paths and apply exclusion
    directories = {os.path.dirname(file) for file in all_files if file and not is_excluded(file)}

    # Convert the set back to a list (which is inherently unique)
    unique_directories = list(directories)
    
    # Sort the output (optional, to keep output organized)
    unique_directories.sort()

    return unique_directories

if __name__ == "__main__":
    # Use argparse to handle command-line arguments
    parser = argparse.ArgumentParser(description="Get a list of directories with changed or new files in the current Git branch.")
    
    # Add argument for exclusions
    parser.add_argument('--exclusions', nargs='+', default=[], help="List of paths or patterns to exclude, e.g. --exclusions 'path/one' '*.md' '.github'")

    # Parse the arguments
    args = parser.parse_args()

    # Call the function with the exclusions passed in via the command line
    directories = get_changed_and_new_directories(args.exclusions)
    
    # Output the result
    print(directories)
