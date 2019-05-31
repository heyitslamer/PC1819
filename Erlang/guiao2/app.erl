-module(app).
-export([start/0, create_account/2, close_account/2, login/2, logout/2, loop/1]).

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

login(L, PW) ->
  ?MODULE ! { self(), { login, L, PW } },
  receive
    {?MODULE, Res} -> Res
  end.

logout(L, PW) ->
  ?MODULE ! { self(), { logout, L, PW } },
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
			case maps:find(L, M) of
        {ok, {P, _}} ->
					From ! { ?MODULE, ok },
					loop(maps:remove(L, M));
				_ ->
					From ! { ?MODULE, invalid },
			       	 	loop(M)       
			end;
		{From, {login, L, P}} ->
			case maps:find(L, M) of
				{ok, {P, false}} ->
					From ! { ?MODULE, ok },
					loop(maps:update(L, { P, true }, M));
				_ -> 
					From ! { ?MODULE, invalid },
					loop(M)
			end;
		{From, {logout, L, P}} ->
			case maps:find(L, M) of
				{ok, {P, true}} ->
					From ! { ?MODULE, ok },
					loop(maps:update(L, { P, false }, M));
				_ -> 
					From ! { ?MODULE, invalid },
					loop(M)
			end
	end. 
