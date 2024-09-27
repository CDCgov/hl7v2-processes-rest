import subprocess
import os
import fnmatch
import argparse
import json

def branch_exists(branch):
    """Check if a branch exists in the local or remote repository."""
    result = subprocess.run(['git', 'rev-parse', '--verify', branch], stdout=subprocess.PIPE, stderr=subprocess.PIPE)
    return result.returncode == 0

def get_changed_and_new_directories(base_branch="main", exclude_patterns=None, debug=False):
    if exclude_patterns is None:
        exclude_patterns = []

    # Check if the base branch exists
    if not branch_exists(base_branch):
        print(f"Error: Base branch '{base_branch}' does not exist.")
        return []

    # Add /.github exclusion and ensure it covers all subdirectories
    exclude_patterns.append('.github')
    exclude_patterns.append('.github/*')

    # Helper function to check if a path matches any exclusion pattern
    def is_excluded(path):
        if any(fnmatch.fnmatch(path, pattern) for pattern in exclude_patterns):
            return True
        directory = os.path.dirname(path)
        return any(fnmatch.fnmatch(directory, pattern) for pattern in exclude_patterns)

    # Compare the current branch with the base branch using --name-status
    diff_command = ['git', 'diff', '--name-status', f'{base_branch}...HEAD']
    compare_diff = subprocess.run(diff_command, stdout=subprocess.PIPE, text=True)
    diff_output = compare_diff.stdout.strip().split('\n')

    # Process the diff output and extract file paths
    all_files = []
    for line in diff_output:
        if line:
            # Each line starts with the status code (A, M, D) followed by the file path
            status, file = line.split('\t', 1)
            if status != 'D':  # We skip deleted files, as we only care about new/modified directories
                all_files.append(file)

    # Print debug output if --debug is specified
    if debug:
        print(f"Detected files (compared with {base_branch}):", all_files)

    # Extract directories from file paths and apply exclusion
    directories = set()
    for file in all_files:
        if not is_excluded(file):
            directory = os.path.dirname(file) or '.'
            directories.add(directory)
        else:
            if debug:
                print(f"Excluded: {file}")

    unique_directories = list(directories)
    unique_directories.sort()

    return unique_directories

if __name__ == "__main__":
    parser = argparse.ArgumentParser(description="Get a list of directories with changed or new files in the current Git branch.")
    parser.add_argument('--exclusions', nargs='+', default=[], help="List of paths or patterns to exclude, e.g. --exclusions 'path/one' '*.md' '.github'")
    parser.add_argument('--base-branch', default='main', help="Base branch to compare with (default is 'main').")
    parser.add_argument('--debug', action='store_true', help="Print debug information.")
    args = parser.parse_args()

    directories = get_changed_and_new_directories(args.base_branch, args.exclusions, args.debug)
    
    if directories:
        paths = f"'{json.dumps(directories)}'"
        print(paths)
    else:
        print("No changes detected or base branch does not exist.")
