#!/bin/sh
seq 0 699 | xargs -i wget http://www.5184.com/gk/common/checkcode.php -O img/{}.png
