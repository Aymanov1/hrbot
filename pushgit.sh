	#!/bin/bash
	clear
    git flow feature start new.project
    git add .
    git commit -m "new.project"
    git flow feature finish new.project
    git push origin develop
