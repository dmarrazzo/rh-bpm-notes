#!/usr/bin/expect

set timeout 13
# set password [lindex $argv 0]
set password "yourpassword"
set command --
if {[llength $argv] == 1} {
	set command [lindex $argv 0]
}
spawn git push $command
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
