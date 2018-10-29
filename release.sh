#!/bin/bash
user=`whoami`
#tar -czf overall_all_images.tar.gz "$1"
#zip -r overall_all_images.tar.gz.apk overall_all_images.tar.gz
#cp overall_all_images.tar.gz.apk /data/mine/test/MT6572/"$user"/
oldext="txt"
newext="java"
dir=$(eval pwd)
for file in $(ls $dir | grep .$oldext)
	do
	name=$(ls $file | cut -d. -f1)
	mv $file ${name}.$newext
	done
echo "change JAVA ======> TXT done!"
