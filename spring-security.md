
# Application Security

I want to secure my application with **Spring Security** and add the **necessary** changes.
You can tell me when **I'm** wrong or what a **better** option would be.

## **Technology** and Wishes

I want to use JWT for **authentication (auth)**. Users should be able to register and log in with a **username** + password. There are different types of roles/users.
Update the `User` class to add the **necessary** fields for security and configuration.

## **Unregistered** User

A **non-registered** user can:

- Register.
- See **active character** info.
- See official combos **for** a character.
- See active tournaments.
- See **rankings** for characters, teams, and the top 25 players (by tournaments **won**).

## **Registered** User

A **registered** user can:

- See **active character** info.
- See official combos **for** a character.
- See active tournaments.
- See **rankings** for characters, teams, and the top 25 players (by tournaments **won**).
- Log in.
- Log out.
- Edit **their** own user profile.
- Find other users.
- View other **users' profiles**. A user profile **shows the teams from recent** games, along with personal stats for **those teams**.
- Create a personal combo. By default, a personal combo is private and **unofficial**.
- See community combos. Users can filter by **latest updates** or by popularity.
- Modify combos **they created**.
- Delete a combo **they created**.
- Change the visibility of **their own** combos from private to public.
- See if **they are registered** for a tournament.
- Cancel **their registration** if the tournament **hasn't** started yet.
- Create a tournament. When this **happens, they transform into an Organizer** user.

## **Organizer** User

- Can do the same things as a **registered** user, but **cannot register for their own** tournaments.
- Modify tournaments **they own**.
- Close **registrations** for a tournament **they own**.
- Cancel a tournament up to 3 days before it **starts**.
- **Request match** creation, add game results, and add tournament results.

## Admin User

- When an admin user creates a combo, **it is an** official combo.
- Modify and delete any combo.
- Modify and delete any tournament.
- **Add**, modify, and delete characters.
- Use all endpoints and operations.
