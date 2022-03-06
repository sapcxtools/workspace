#!/bin/zsh

function updateExtension() {
	if [ -z "$1" ]; then
		echo -e "\e[31m [WARN] You need to specify an extension name, eg. 'sapcommercetoolkit'! \e[39m"
		return 1
	fi

    if [ -d "$PREFIX/$1" ]; then
        echo -e "\e[32m [INFO] Updating $1! \e[39m"
        git subtree pull --message "update extension $1 from SAP CX Tools" --prefix=$PREFIX/$1 git@github.com:sapcxtools/$1.git main
    else
        echo -e "\e[31m [WARN] No extension found with name $1 at $PREFIX! \e[39m"
    fi
}



export PREFIX=core-customize/hybris/bin/custom/sapcxtools
if [ ! -d "$PREFIX" ]; then
    echo -e "\e[31m [WARN] You need to run this script from the repository root! \e[39m"
    exit 1
fi 

export BRANCH=`git rev-parse --abbrev-ref HEAD`
if [ ! "$BRANCH" = "develop" ]; then
    echo -e "\e[31m [WARN] You need to checkout the develop branch before executing this script! \e[39m"
    exit 2
fi

updateExtension sapcxtemplate
updateExtension sapcommercetoolkit
updateExtension sapcxbackoffice
updateExtension sapcxreporting
