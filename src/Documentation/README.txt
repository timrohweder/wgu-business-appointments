A.   Create a log-in form that can determine the user’s location and translate log-in and error control messages (e.g., “The username and password did not match.”) into two languages.

        -- Finished (English and Spanish)

B.   Provide the ability to add, update, and delete customer records in the database, including name, address, and phone number.

        -- Finished

C.   Provide the ability to add, update, and delete appointments, capturing the type of appointment and a link to the specific customer record in the database.

        -- Finished (the customer record link is a button for when an appointment is selected)

D.   Provide the ability to view the calendar by month and by week.

        -- Finished

E.    Provide the ability to automatically adjust appointment times based on user time zones and daylight saving time.

        -- Finished

F.   Write exception controls to prevent each of the following. You may use the same mechanism of exception control more than once, but you must incorporate at least  two different mechanisms of exception control.
•   scheduling an appointment outside business hours
•   scheduling overlapping appointments
•   entering nonexistent or invalid customer data
•   entering an incorrect username and password

        -- Finished. Most exceptions employ only a try-catch block. My second form of exception control is a throws clause, used in checkIfTextFieldEmpty() in EditCustomerController and AddCustomerController.

G.  Write two or more lambda expressions to make your program more efficient, justifying the use of each lambda expression with an in-line comment.

        -- Finished. Commented lambdas are found in AddAppointmentController and CalendarViewController.

H.   Write code to provide an alert if there is an appointment within 15 minutes of the user’s log-in.

        -- Finished.

I.   Provide the ability to generate each  of the following reports:
•   number of appointment types by month
•   the schedule for each consultant
•   one additional report of your choice

        -- Finished. My additional report counts the total current number of appointments, consultants, and customers.

J.   Provide the ability to track user activity by recording timestamps for user log-ins in a .txt file. Each new record should be appended to the log file, if the file already exists.

        -- Finished. logLogin(), found in LoginViewController, handles the logging.

K. Demonstrate professional communication in the content and presentation of your submission.

        -- Finished.