create table task (
    id serial,
    name varchar (100),
    created date,
    status boolean,
    completed date,
    user_id int,
    primary key (id),
    CONSTRAINT fk_user_id
        FOREIGN KEY(user_id)
            REFERENCES users(id)
);