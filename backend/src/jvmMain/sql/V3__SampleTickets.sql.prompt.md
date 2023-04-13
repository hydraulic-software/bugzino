Generate at least 100 sample tickets for testing purposes, imagining that they're issues filed in the issue tracker of a typical software project, filling out the rest of the list.

```sql
INSERT INTO tickets (title, description, assignee) VALUES ('Write a demo app for KotlinConf', 'It should demo the use of Jetpack Compose, Conveyor and PostgreSQL', 'alice');
INSERT INTO tickets (title, description, assignee) VALUES ('Polish the user interface', 'It could be prettier!', 'bob');
INSERT INTO tickets (title, description, assignee) VALUES ('Fix the bugs', 'There are a lot of crashes.', 'mike');
```

The `assignee` field must ONLY be one of 'alice', 'bob' or 'mike'. Don't use any other names.
