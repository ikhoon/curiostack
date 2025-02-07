/*
 * MIT License
 *
 * Copyright (c) 2019 Choko (choko@curioswitch.org)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

import { injectReducer, injectSaga } from '@curiostack/base-web';
import { Grid, GridList, GridListTile } from '@material-ui/core';
import { push } from 'connected-react-router';
import { LocationDescriptorObject } from 'history';
import React, { useCallback } from 'react';
import { Helmet } from 'react-helmet';
import { hot } from 'react-hot-loader/root';
import { WrappedComponentProps, injectIntl } from 'react-intl';
import { connect } from 'react-redux';
import { Link } from 'react-router-dom';
import { compose } from 'redux';
import { InjectedFormProps } from 'redux-form';
import { Field, reduxForm } from 'redux-form/immutable';
import styled from 'styled-components';

import BottomSheet from '../../components/BottomSheet';
import Map from '../../components/Map';
import SearchBox, { SearchBoxProps } from '../../components/SearchBox';

import { DispatchProps, mapDispatchToProps } from './actions';
import messages from './messages';
import reducer, { StateProps } from './reducer';
import saga from './saga';
import selectHomePage from './selectors';

interface OwnDispatchProps extends DispatchProps {
  doPush: (url: string) => void;
}

type Props = OwnDispatchProps & WrappedComponentProps & StateProps;

const SearchBoxWrapper = styled.div`
  position: absolute;
  top: 10px;
  width: 100%;
  height: 100%;
  z-index: 100;
  background: transparent;
  pointer-events: none;

  ${SearchBox} {
    pointer-events: auto;
  }
`;

const SearchBoxContainer: React.FunctionComponent<SearchBoxProps> = React.memo(
  (props) => (
    <SearchBoxWrapper>
      <Grid container justify="center" alignItems="center">
        <Grid item xs={11} md={6}>
          {/* eslint-disable-next-line react/jsx-props-no-spreading */}
          <Field name="query" component={SearchBox} {...props} />
        </Grid>
      </Grid>
    </SearchBoxWrapper>
  ),
);

const HomePage: React.FunctionComponent<Props & InjectedFormProps> = React.memo(
  (props) => {
    const {
      bottomSheetVisibility,
      doPush,
      doSearch,
      getLandmarks,
      getPlaces,
      handleSubmit,
      landmarks,
      places,
      intl: { formatMessage: _ },
      setBottomSheetOpen,
      setMap,
    } = props;

    const openPlacePage = useCallback((id: string) => {
      doPush(`/place/${id}`);
    }, []);

    const handleSearchSubmit = useCallback(
      handleSubmit(() => {
        doSearch();
      }),
      [doSearch],
    );

    const updateMap = useCallback(() => {
      getPlaces();
      getLandmarks();
    }, [getPlaces, getLandmarks]);

    return (
      <>
        <Helmet title={_(messages.title)} />
        <form onSubmit={handleSearchSubmit}>
          <SearchBoxContainer onSearch={handleSearchSubmit} />
        </form>
        <Map
          doUpdateMap={updateMap}
          doSetMap={setMap}
          onOpenPlace={openPlacePage}
          landmarks={landmarks}
          places={places}
        />
        <BottomSheet
          visibility={bottomSheetVisibility}
          setOpen={setBottomSheetOpen}
        >
          <GridList>
            {places.map((place) => (
              <GridListTile>
                <Link to={`/place/${place.getId()}`}>
                  <img alt={place.getName()} src={place.getPrimaryPhotoUrl()} />
                </Link>
              </GridListTile>
            ))}
          </GridList>
        </BottomSheet>
      </>
    );
  },
);

const withConnect = connect(
  selectHomePage,
  (dispatch) => ({
    ...mapDispatchToProps(dispatch),
    doPush: (location: LocationDescriptorObject) => dispatch(push(location)),
  }),
);
const withReducer = injectReducer({
  reducer,
  key: 'homePage',
});
const withSaga = injectSaga({ saga, key: 'homePage' });

export default compose(
  injectIntl,
  withReducer,
  withSaga,
  withConnect,
  reduxForm({
    form: 'homePage',
  }),
  hot,
)(HomePage);
