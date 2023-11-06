create table friends(

    friend_id bigint not null,
    user_id bigint not null,

    primary key(friend_id, user_id),

    foreign key(friend_id) references users(id),
    foreign key(user_id) references users(id)

);
