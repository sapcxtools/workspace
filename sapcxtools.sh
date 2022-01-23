#!/bin/sh

#######################
# SAP CX TOOLS        #
#######################

function cx_add() {
	if [ -z "$1" ]; then
		echo -e "\e[31m [WARN] You need to specify an extension name, eg. sapcommercetoolkit! \e[39m"
		return 1
	fi

	if [ -d "$SAPCXTOOLS_HOME" ]; then
		toworkspace
		git subtree add --squash --message "add extension $1 from SAP CX Tools" --prefix=$SAPCXTOOLS_PREFIX/$1 git@github.com:sapcxtools/$1.git main
	else
		echo -e "\e[31m [WARN] No SAP CX Tools found at $SAPCXTOOLS_HOME! \e[39m"
	fi
}

function cx_upgrade() {
	if [ -z "$1" ]; then
		echo -e "\e[31m [WARN] You need to specify an extension name, eg. sapcommercetoolkit! \e[39m"
		return 1
	fi

	if [ -d "$SAPCXTOOLS_HOME" ]; then
		if [ -d "$SAPCXTOOLS_HOME/$1" ]; then
			toworkspace
			git subtree pull --squash --message "update extension $1 from SAP CX Tools" --prefix=$SAPCXTOOLS_PREFIX/$1 git@github.com:sapcxtools/$1.git main
		else
			echo -e "\e[31m [WARN] No extension found with name $1 at $SAPCXTOOLS_HOME! \e[39m"
			echo -e "\e[32m [INFO] Available extensions are:\e[39m"

			find $SAPCXTOOLS_HOME -type d -mindepth 1 -maxdepth 1 -exec basename {} \; | sort -r | while read x; do
				echo -e "\e[32m        --> $x \e[39m"
			done
		fi
	else
		echo -e "\e[31m [WARN] No SAP CX Tools found at $SAPCXTOOLS_HOME! \e[39m"
	fi
}

function cx_pullrequest() {
	if [ -z "$1" ]; then
		echo -e "\e[31m [WARN] You need to specify an extension name, eg. 'sapcommercetoolkit'! \e[39m"
		return 1
	fi
	if [ -z "$2" ]; then
		echo -e "\e[31m [WARN] You need to specify a name for the feature branch, eg. 'additional-unit-tests'! \e[39m"
		return 1
	fi

	if [ -d "$SAPCXTOOLS_HOME" ]; then
		if [ -d "$SAPCXTOOLS_HOME/$1" ]; then
			toworkspace
			git subtree push --prefix=$SAPCXTOOLS_PREFIX/$1 git@github.com:sapcxtools/$1.git feature/$2
		else
			echo -e "\e[31m [WARN] No extension found with name $1 at $SAPCXTOOLS_HOME! \e[39m"
			echo -e "\e[32m [INFO] Available extensions are:\e[39m"

			find $SAPCXTOOLS_HOME -type d -mindepth 1 -maxdepth 1 -exec basename {} \; | sort -r | while read x; do
				echo -e "\e[32m        --> $x \e[39m"
			done
		fi
	else
		echo -e "\e[31m [WARN] No SAP CX Tools found at $SAPCXTOOLS_HOME! \e[39m"
	fi
}
