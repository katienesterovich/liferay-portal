/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

import React, {createContext, useReducer} from 'react';

const LOAD_DATA = 'LOAD_DATA';
const SET_ERROR = 'SET_ERROR';
const SET_DATA = 'SET_DATA';

const INITIAL_STATE = {
	data: null,
	error: null,
	loading: false,
};

const noop = () => {};

export const StoreDispatchContext = createContext(noop);
export const StoreStateContext = createContext([INITIAL_STATE, noop]);

function reducer(state = INITIAL_STATE, action) {
	let nextState = state;

	switch (action.type) {
		case LOAD_DATA:
			nextState = {
				...state,
				loading: true,
			};
			break;

		case SET_ERROR:
			nextState = {
				...state,
				error: action.error,
				loading: false,
			};
			break;

		case SET_DATA:
			nextState = {
				data: {
					...action.data,
				},
				error: action.data?.error,
				loading: action?.loading || false,
			};
			break;

		default:
			return state;
	}

	return nextState;
}

export function StoreContextProvider({children, value}) {
	const [state, dispatch] = useReducer(reducer, {...INITIAL_STATE, ...value});

	return (
		<StoreDispatchContext.Provider value={dispatch}>
			<StoreStateContext.Provider value={state}>
				{children}
			</StoreStateContext.Provider>
		</StoreDispatchContext.Provider>
	);
}
