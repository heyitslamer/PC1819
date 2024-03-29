-module(servidor).
-export([server/1]).

server(Port) ->
	Room = spawn(fun() -> room([]) end),
	{ok, LSock} = gen_tcp:listen(Port, [binary, {packet, line}]),
	acceptor(LSock, Room).

acceptor(LSock, Room) ->
	{ok, Sock} = gen_tcp:accept(LSock),
	Room ! {new_user, Sock},
	gen_tcp:controlling_process(Sock, Room),
	acceptor(LSock, Room).

room(Sockets) ->
	receive
		{new_user, Sock} ->
			io:format("new_user~n", []),
			room([Sock | Sockets]);
		{tcp, _, Data} ->
			io:format("received ~p~n", [Data]),
			[gen_tcp:send(Socket, Data) || Socket <- Sockets ],
			room(Sockets);
		{tcp_closed, Sock} ->
			io:format("user_disconnected~n", []),
			room(Sockets -- [Sock]);
		{tcp_error, Sock, _} ->
			io:format("tcp_error~n", []),
			room(Sockets -- [Sock])
	end.
