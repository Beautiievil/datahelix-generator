import {connect} from "react-redux";
import {Dispatch} from "redux";

import * as React from "react";
import {Dropdown, DropdownProps} from "semantic-ui-react";
import Actions from "../redux/actions";
import selectFieldLookup from "../redux/selectors/selectFieldLookup";
import {FieldKinds, IAppState} from "../redux/state/IAppState";

interface IProps extends DropdownProps
{
	fieldId: string;
}

function mapStateToProps(state: IAppState, ownProps: IProps): DropdownProps
{
	const fieldState = selectFieldLookup(state)[ownProps.fieldId];

	return {
		options: [
			{ text: "Numeric", value: FieldKinds.Numeric },
			{ text: "String", value: FieldKinds.String }
		],
		value: fieldState.restrictions.kind
	};
}

function mapDispatchToProps(dispatch: Dispatch, ownProps: IProps): DropdownProps
{
	return {
		onChange: (event: React.SyntheticEvent<HTMLElement>, data: DropdownProps) => {
			dispatch(Actions.ChangeFieldKind.create({
				fieldId: ownProps.fieldId,
				newKind: data.value === undefined
					? FieldKinds.Unclassified
					: data.value as FieldKinds
			}));
		}
	};
}

const WrappedComponent = connect(
	mapStateToProps,
	mapDispatchToProps,
	(
		s: DropdownProps,
		d: DropdownProps,
		{ fieldId, ...rest }: IProps
	) => ({ ...s, ...d, ...rest })) // don't pass fieldId prop down (Redux passes down by default)
(Dropdown);

export default WrappedComponent;
