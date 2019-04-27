-module(manager).
-export([start/0, create_account/2, close_account/2, loop/1]).

% interface functions

start() ->
	register(?MODULE, spawn(fun() -> loop(maps:new()) end)).

create_account(L, PW) ->
	?MODULE ! { self(), { create, L, PW } },
	receive 
		{?MODULE, Res} -> Res
	end.

close_account(L, PW) ->
	?MODULE ! { self(), { close, L, PW } },
	receive
		{?MODULE, Res} -> Res
	end.

% process

loop(M) ->
	receive
		{From, {create, L, P}} -> 
			case maps:find(L, M) of
				error ->
					From ! { ?MODULE, ok },
					loop(maps:put(L, { P, true }, M));
				_ -> 
					From ! { ?MODULE, user_exists },
					loop(M)
			end;	
		{From, {close, L, P}} ->
			if 
				maps:is_key(L, M) ->
					From ! { ?MODULE, ok },
					loop(maps:remove(L, M));
				true ->
					From ! { ?MODULE, invalid },
			       	 	loop(M)       
			end;
		{From, {login, L, P}} ->
			case maps:find(L, M) of
				{ok, {P, true}} ->
					From ! { ?MODULE, invalid },
					loop(M);
				{ok, {P, false}} ->
					From ! { ?MODULE, ok },
					loop(maps:update(L, { P, true }, M));
				_ -> 
					From ! { ?MODULE, invalid },
					loop(M)
			end;
	end.


