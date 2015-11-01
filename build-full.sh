set -e

if [ -e virtlua ]
then
	(cd virtlua && git pull && cd ..)
else
	git clone --depth=1 https://github.com/cosmicdrift/virtlua
fi

ant
