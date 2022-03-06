#!/bin/zsh

function releaseExtension() {
	if [ -z "$1" ]; then
		echo -e "\e[31m [WARN] You need to specify an extension name, eg. 'sapcommercetoolkit'! \e[39m"
		return 1
	fi
	if [ -z "$2" ]; then
		echo -e "\e[31m [WARN] You need to specify a name for the release, eg. '1.0.0'! \e[39m"
		return 1
	fi

    if [ -d "$PREFIX/$1" ]; then
        echo -e "\e[32m [INFO] Releasing $1@$2! \e[39m"
        git subtree push --prefix=$PREFIX/$1 git@github.com:sapcxtools/$1.git release/$2
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
if [ ! "$BRANCH" = "main" ]; then
    echo -e "\e[31m [WARN] You need to checkout the main branch before executing this script! \e[39m"
    exit 2
fi

if [ -z "$1" ]; then
    echo -e "\e[31m [WARN] You need to specify a name for the release, eg. '1.0.0'! \e[39m"
    exit 3
fi

releaseExtension sapcxtemplate $1
releaseExtension sapcommercetoolkit $1
releaseExtension sapcxbackoffice $1
releaseExtension sapcxreporting $1
