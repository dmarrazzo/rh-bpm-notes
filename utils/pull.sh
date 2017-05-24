#!/usr/bin/expect

set timeout 13
# set password [lindex $argv 0]
set password "yourpassword"
send_user "$argv\n"
spawn git pull
expect {
    "(yes/no)?"
        {
            send "yes\n"
            expect "*assword:" { send "$password\r"}
        }
    "*assword:"
        {
            send "$password\r"
        }
    }
expect eof
