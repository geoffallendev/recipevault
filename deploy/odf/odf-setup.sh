# ODF
#oc new-project openshift-storage
#oc apply -f ./deploy/odf/odf-operatorgroup.yaml
#oc apply -f ./deploy/odf/odf-operator-sub.yaml
# need to wait for multiple subscriptions
#oc apply -f mcg-storagesystem.yaml
# Need uid to include in storagecluster resource
#oc get storagesystem/ocs-storagecluster-storagesystem -n openshift-storage -o jsonpath='{.metadata.uid}'
perl -pe "s|(STORAGE_SYSTEM_UID:).*$|\$1 oc get storagesystem/ocs-storagecluster-storagesystem -n openshift-storage -o jsonpath='{.metadata.uid}'|" -i deploy/odf/mcg-storagecluster.yaml

