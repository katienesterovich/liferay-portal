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

import ClayAlert from '@clayui/alert';
import ClayButton from '@clayui/button';
import ClayProgressBar from '@clayui/progress-bar';
import {useIsMounted} from '@liferay/frontend-js-react-web';
import {fetch} from 'frontend-js-web';
import PropTypes from 'prop-types';
import React, {useCallback, useContext, useEffect, useState} from 'react';

import {StoreDispatchContext, StoreStateContext} from '../context/StoreContext';
import APIService from '../utils/APIService';
import getPageSpeedProgress from '../utils/getPageSpeedProgress';
import BasicInformation from './BasicInformation';
import EmptyLayoutReports from './EmptyLayoutReports';
import LayoutReportsIssuesList from './LayoutReportsIssuesList';

function loadIssues({data, dispatch, languageId, portletNamespace}) {
	const url = data.canonicalURLs.find(
		(canonicalURL) =>
			canonicalURL.languageId === languageId || data.defaultLanguageId
	);

	if (url) {
		APIService.getLayoutReportsIssues(
			url.layoutReportsIssuesURL,
			portletNamespace
		)
			.then(({layoutReportsIssues}) => {
				dispatch({
					data: {
						...data,
						layoutReportsIssues,
					},
					type: 'SET_DATA',
				});
			})
			.catch(() => {
				dispatch({
					error: {
						buttonTitle: Liferay.Language.get('relaunch'),
						message: Liferay.Language.get('connection-failed'),
					},
					type: 'SET_ERROR',
				});
			});
	}
	else {
		dispatch({
			error: Liferay.Language.get('an-unexpected-error-occurred'),
			type: 'SET_ERROR',
		});
	}
}

export default function LayoutReports({
	eventTriggered,
	isPanelStateOpen,
	layoutReportsDataURL,
	portletNamespace,
}) {
	const isMounted = useIsMounted();

	const {data, error, loading} = useContext(StoreStateContext);

	const dispatch = useContext(StoreDispatchContext);

	const safeDispatch = useCallback(
		(action) => {
			if (isMounted()) {
				dispatch(action);
			}
		},
		[dispatch, isMounted]
	);

	const [percentage, setPercentage] = useState(0);
	const [selectedLanguageId, setSelectedLanguageId] = useState(null);

	useEffect(() => {
		if (loading && !error) {
			const initial = Date.now();
			const interval = setInterval(() => {
				const elapsedTimeInSeconds = (Date.now() - initial) / 1000;
				const progress = getPageSpeedProgress(elapsedTimeInSeconds);

				setPercentage(progress.toFixed(0));
			}, 500);

			return () => clearInterval(interval);
		}
	}, [error, loading]);

	const getData = useCallback(
		(fetchURL) => {
			safeDispatch({type: 'LOAD_DATA'});

			fetch(fetchURL, {method: 'GET'})
				.then((response) =>
					response.json().then((data) => {
						safeDispatch({
							data: {
								...data,
							},
							loading: data.validConnection,
							type: 'SET_DATA',
						});

						if (data.validConnection) {
							loadIssues({
								data,
								dispatch: safeDispatch,
								portletNamespace,
							});
						}
					})
				)
				.catch(() => {
					safeDispatch({
						error: Liferay.Language.get(
							'an-unexpected-error-occurred'
						),
						type: 'SET_ERROR',
					});
				});
		},
		[portletNamespace, safeDispatch]
	);

	useEffect(() => {
		if (isPanelStateOpen) {
			getData(layoutReportsDataURL);
		}
	}, [isPanelStateOpen, layoutReportsDataURL, getData]);

	useEffect(() => {
		if (eventTriggered && !data) {
			getData(layoutReportsDataURL);
		}
	}, [eventTriggered, data, layoutReportsDataURL, getData]);

	const onLanguageChange = (languageId) => {
		setPercentage(0);
		setSelectedLanguageId(languageId);

		safeDispatch({
			data: {
				...data,
				layoutReportsIssues: null,
			},
			loading: true,
			type: 'SET_DATA',
		});

		loadIssues({
			data,
			dispatch: safeDispatch,
			languageId,
			portletNamespace,
		});
	};

	return (
		<>
			{data?.validConnection &&
				error &&
				(error?.message ? (
					<ClayAlert displayType="danger" variant="stripe">
						{error.message}

						<ClayAlert.Footer>
							<ClayButton.Group>
								<ClayButton alert disabled>
									{error.buttonTitle}
								</ClayButton>
							</ClayButton.Group>
						</ClayAlert.Footer>
					</ClayAlert>
				) : (
					<ClayAlert displayType="danger" variant="stripe">
						{error}
					</ClayAlert>
				))}

			<div className="c-p-3">
				{loading && !data?.layoutReportsIssues ? (
					<div className="text-secondary">
						{Liferay.Language.get(
							'connecting-with-google-pagespeed'
						)}
						<ClayProgressBar value={percentage} />
					</div>
				) : (
					data &&
					!error && (
						<>
							<BasicInformation
								canonicalURLs={data.canonicalURLs}
								defaultLanguageId={data.defaultLanguageId}
								onLanguageChange={onLanguageChange}
								selectedLanguageId={selectedLanguageId}
							/>

							{data.validConnection &&
							data?.layoutReportsIssues ? (
								<LayoutReportsIssuesList />
							) : (
								<EmptyLayoutReports />
							)}
						</>
					)
				)}
			</div>
		</>
	);
}

LayoutReports.propTypes = {
	eventTriggered: PropTypes.bool.isRequired,
	isPanelStateOpen: PropTypes.bool.isRequired,
	layoutReportsDataURL: PropTypes.string.isRequired,
};
