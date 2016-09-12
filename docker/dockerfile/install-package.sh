cd /root/packages
#dpkg -i net-tools_1.60-26ubuntu1_amd64.deb
#dpkg -i iputils-ping_3%3a20121221-5ubuntu2_amd64.deb

#cd ./packages
filelist = ""

for file in ./*
do
    if [[ -f $file ]]; then
        echo "$file"
        filelist="$filelist $file"
    fi
done

echo "the file list is : $filelist"

dpkg -i $filelist
