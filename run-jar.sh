#! /bin/bash
#
# Run a class from within the built JAR file.
#
# This script doesn't do much more than build the class path for you
# based on the contents of ext-lib and local-lib.
#
top_dir="${0%/*}"
class_path="$top_dir/bin/jRally.jar"
for sub_dir in "ext-lib" "local-lib"
do
	for name in $top_dir/$sub_dir/*.jar
	do
		class_path="$class_path:$name"
	done
done
java -classpath "$class_path" "$@"
